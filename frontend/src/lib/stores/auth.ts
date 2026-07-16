import { authService } from "@lib/services/auth-service"
import type { ApiUser } from "@lib/api/api-types"
import { writable } from "svelte/store"

export const currentUser = writable<ApiUser | null>(null)

export function mergeCurrentUser(user: ApiUser) {
	currentUser.update((current) =>
		current
			? {
					...current,
					...user,
					email: user.email ?? current.email,
				}
			: user,
	)
}

export async function initializeAuth() {
	const expired = () => currentUser.set(null)
	window.addEventListener("pathora:session-expired", expired)
	try {
		currentUser.set(await authService.me())
	} catch {
		currentUser.set(null)
	}
}

export async function clearSession() {
	try {
		await authService.logout()
	} catch {
		// Local logout must still succeed when the API is unavailable.
	} finally {
		currentUser.set(null)
	}
}
