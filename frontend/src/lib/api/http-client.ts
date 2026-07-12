const API_URL = import.meta.env.VITE_API_URL ?? "http://localhost:4000/v1"

export class ApiError extends Error {
	constructor(
		message: string,
		public status: number,
		public details?: unknown,
	) {
		super(message)
	}
}

type ApiEnvelope<T> = { success: boolean; message: string; data: T }
type RequestOptions = Omit<RequestInit, "body"> & {
	body?: unknown
	authenticated?: boolean
}

export const tokenStorage = {
	get: () => localStorage.getItem("pathora_access_token"),
	getRefresh: () => localStorage.getItem("pathora_refresh_token"),
	set: (accessToken: string, refreshToken?: string) => {
		localStorage.setItem("pathora_access_token", accessToken)
		if (refreshToken) localStorage.setItem("pathora_refresh_token", refreshToken)
	},
	clear: () => {
		localStorage.removeItem("pathora_access_token")
		localStorage.removeItem("pathora_refresh_token")
	},
}

type RefreshResponse = { accessToken: string; refreshToken: string }
let refreshPromise: Promise<void> | null = null

async function refreshSession() {
	const refreshToken = tokenStorage.getRefresh()
	if (!refreshToken) throw new Error("No refresh token available.")
	const response = await fetch(`${API_URL}/auth/refresh`, {
		method: "POST",
		headers: { Accept: "application/json", "Content-Type": "application/json" },
		body: JSON.stringify({ refreshToken }),
	})
	const payload = await response.json().catch(() => null)
	if (!response.ok) throw new ApiError(payload?.message ?? "Session expired.", response.status, payload)
	const tokens = (payload as ApiEnvelope<RefreshResponse>).data
	tokenStorage.set(tokens.accessToken, tokens.refreshToken)
}

async function ensureFreshSession() {
	refreshPromise ??= refreshSession()
		.catch((error) => {
			tokenStorage.clear()
			window.dispatchEvent(new CustomEvent("pathora:session-expired"))
			throw error
		})
		.finally(() => { refreshPromise = null })
	return refreshPromise
}

export async function apiRequest<T>(
	path: string,
	options: RequestOptions = {},
): Promise<T> {
	return executeRequest<T>(path, options, true, 8)
}

async function executeRequest<T>(path: string, options: RequestOptions, canRefresh: boolean, retries: number): Promise<T> {
	const method = (options.method ?? "GET").toUpperCase()
	const headers = new Headers(options.headers)
	headers.set("Accept", "application/json")
	if (options.body !== undefined)
		headers.set("Content-Type", "application/json")
	if (options.authenticated) {
		const token = tokenStorage.get()
		if (token) headers.set("Authorization", `Bearer ${token}`)
	}
	const response = await fetch(`${API_URL}${path}`, {
		...options,
		headers,
		body: options.body === undefined ? undefined : JSON.stringify(options.body),
	})
	if (response.status === 204) return undefined as T
	const payload = await response.json().catch(() => null)
	if ([502, 503, 504].includes(response.status) && method === "GET" && retries > 0) {
		await new Promise((resolve) => setTimeout(resolve, 1200))
		return executeRequest<T>(path, options, canRefresh, retries - 1)
	}
	if (response.status === 401 && options.authenticated && canRefresh) {
		await ensureFreshSession()
		return executeRequest<T>(path, options, false, retries)
	}
	if (!response.ok)
		throw new ApiError(
			payload?.message ?? "No pudimos completar la solicitud.",
			response.status,
			payload,
		)
	return (payload as ApiEnvelope<T>).data
}

export function queryString(
	values: Record<string, string | number | boolean | undefined | null>,
) {
	const query = new URLSearchParams()
	Object.entries(values).forEach(([key, value]) => {
		if (value !== undefined && value !== null && value !== "")
			query.set(key, String(value))
	})
	const serialized = query.toString()
	return serialized ? `?${serialized}` : ""
}
