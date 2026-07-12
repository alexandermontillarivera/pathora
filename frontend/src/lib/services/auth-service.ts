import { apiRequest, tokenStorage } from "@lib/api/http-client"
import type { ApiUser, AuthResponse } from "@lib/api/api-types"

export const authService = {
	async login(email: string, password: string) {
		const result = await apiRequest<AuthResponse>("/auth/login", {
			method: "POST",
			body: { email, password },
		})
		tokenStorage.set(result.accessToken, result.refreshToken)
		return result
	},
	async register(input: {
		firstName: string
		lastName: string
		email: string
		password: string
		description?: string
	}) {
		const result = await apiRequest<AuthResponse>("/auth/register", {
			method: "POST",
			body: input,
		})
		tokenStorage.set(result.accessToken, result.refreshToken)
		return result
	},
	me: () => apiRequest<ApiUser>("/auth/me", { authenticated: true }),
	forgotPassword: (email: string) =>
		apiRequest<void>("/auth/forgot-password", {
			method: "POST",
			body: { email },
		}),
	resetPassword: (token: string, password: string) =>
		apiRequest<void>("/auth/reset-password", {
			method: "POST",
			body: { token, password },
		}),
	async logout() {
		const refreshToken = tokenStorage.getRefresh()
		try {
			if (refreshToken) await apiRequest<void>("/auth/logout", { method: "POST", body: { refreshToken } })
		} finally {
			tokenStorage.clear()
		}
	},
}
