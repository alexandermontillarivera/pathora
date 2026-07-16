import { apiRequest } from "@lib/api/http-client"
import type { ApiUser } from "@lib/api/api-types"

export const userService = {
	find: (id: number) => apiRequest<ApiUser>(`/users/${id}`),
	update: (input: {
		firstName: string
		lastName: string
		description: string
		country?: string
		avatarSeed?: string
	}) =>
		apiRequest<ApiUser>("/users/me", {
			method: "PUT",
			authenticated: true,
			body: input,
		}),
	removeCurrent: () =>
		apiRequest<void>("/users/me", {
			method: "DELETE",
			authenticated: true,
		}),
}
