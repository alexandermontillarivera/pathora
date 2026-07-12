import type { PageResponse, SavedCareer } from "@lib/api/api-types"
import { apiRequest, queryString } from "@lib/api/http-client"

export const savedService = {
	list: (page = 1, max = 20) =>
		apiRequest<PageResponse<SavedCareer>>(
			`/me/saved-careers${queryString({ page, max, order: "DESC" })}`,
			{ authenticated: true },
		),
	add: (careerId: number) =>
		apiRequest<SavedCareer>(`/me/saved-careers/${careerId}`, {
			method: "POST",
			authenticated: true,
		}),
	remove: (careerId: number) =>
		apiRequest<void>(`/me/saved-careers/${careerId}`, {
			method: "DELETE",
			authenticated: true,
		}),
}
