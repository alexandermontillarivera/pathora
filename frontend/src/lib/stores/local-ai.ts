import { writable } from "svelte/store"

export const LOCAL_AI_MODEL = "Qwen2.5-3B-Instruct-q4f16_1-MLC"

export type LocalAiStatus =
	| "disabled"
	| "idle"
	| "downloading"
	| "ready"
	| "unsupported"
	| "error"

export type LocalAiState = {
	enabled: boolean
	cached: boolean
	status: LocalAiStatus
	progress: number
	progressText: string
	error: string
}

const initialState: LocalAiState = {
	enabled: false,
	cached: false,
	status: "disabled",
	progress: 0,
	progressText: "",
	error: "",
}

export const localAi = writable<LocalAiState>(initialState)
let preferenceKey: string | null = null

export function setLocalAiUser(userId: number | null) {
	preferenceKey = userId ? `pathora:local-ai:${userId}` : null
	const enabled = Boolean(
		preferenceKey && localStorage.getItem(preferenceKey) === "enabled",
	)
	localAi.set({
		...initialState,
		enabled,
		status: enabled ? "idle" : "disabled",
	})
}

export function persistLocalAiPreference(enabled: boolean) {
	if (!preferenceKey) return
	enabled
		? localStorage.setItem(preferenceKey, "enabled")
		: localStorage.removeItem(preferenceKey)
}

export function updateLocalAi(patch: Partial<LocalAiState>) {
	localAi.update((current) => ({ ...current, ...patch }))
}
