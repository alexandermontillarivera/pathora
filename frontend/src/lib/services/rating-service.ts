import type { ApiRating, PageResponse, RatingSummary } from "@lib/api/api-types"
import { apiRequest, queryString } from "@lib/api/http-client"

export const ratingService = {
	summary: (careerId: number) =>
		apiRequest<RatingSummary>(`/careers/${careerId}/ratings`),
	mine: (careerId: number) =>
		apiRequest<ApiRating | null>(`/careers/${careerId}/ratings/me`, {
			authenticated: true,
		}),
	byUser: (userId: number, page = 1, max = 20) =>
		apiRequest<PageResponse<ApiRating>>(
			`/users/${userId}/ratings${queryString({ page, max, order: "DESC" })}`,
		),
	create: (careerId: number, value: number) =>
		apiRequest<ApiRating>(`/careers/${careerId}/ratings`, {
			method: "POST",
			authenticated: true,
			body: { value },
		}),
	update: (id: number, value: number) =>
		apiRequest<ApiRating>(`/ratings/${id}`, {
			method: "PUT",
			authenticated: true,
			body: { value },
		}),
	remove: (id: number) =>
		apiRequest<void>(`/ratings/${id}`, {
			method: "DELETE",
			authenticated: true,
		}),
}
