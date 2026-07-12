import { authService } from "@lib/services/auth-service"
import type { ApiUser } from "@lib/api/api-types"
import { writable } from "svelte/store"

export const currentUser = writable<ApiUser | null>(null)

export async function initializeAuth() {
	const expired = () => currentUser.set(null)
	window.addEventListener("pathora:session-expired", expired)
	try {
		currentUser.set(await authService.me())
	} catch {
		currentUser.set(null)
	}
}

export function clearSession() {
	void authService.logout()
	currentUser.set(null)
}
