import { apiRequest, queryString } from "@lib/api/http-client"
import type { ApiComment, PageResponse } from "@lib/api/api-types"

export const commentService = {
	stats: () => apiRequest<{averageRating:number;comments:number;careers:number}>('/community/stats'),
	recent: (page = 1, max = 10) =>
		apiRequest<PageResponse<ApiComment>>(
			`/comments${queryString({ page, max, order: "DESC" })}`,
		),
	byUser: (userId: number, page = 1, max = 20) =>
		apiRequest<PageResponse<ApiComment>>(`/users/${userId}/comments${queryString({ page, max, order: "DESC" })}`),
	list: (careerId: number, page = 1, max = 10) =>
		apiRequest<PageResponse<ApiComment>>(
			`/careers/${careerId}/comments${queryString({ page, max, order: "DESC" })}`,
		),
	create: (careerId: number, content: unknown) =>
		apiRequest<ApiComment>(`/careers/${careerId}/comments`, {
			method: "POST",
			authenticated: true,
			body: {
				content:
					typeof content === "string"
						? { type: "html", html: content }
						: content,
			},
		}),
	update: (id: number, content: unknown) => apiRequest<ApiComment>(`/comments/${id}`, { method: "PUT", authenticated: true, body: { content: typeof content === "string" ? { type: "html", html: content } : content } }),
	remove: (id: number) => apiRequest<void>(`/comments/${id}`, { method: "DELETE", authenticated: true }),
	reply: (commentId: number, content: unknown) =>
		apiRequest<ApiComment>(`/comments/${commentId}/replies`, {
			method: "POST",
			authenticated: true,
			body: {
				content:
					typeof content === "string"
						? { type: "html", html: content }
						: content,
			},
		}),
	vote: (commentId: number, useful: boolean) =>
		apiRequest<{ useful: number; notUseful: number; currentVote: boolean }>(
			`/comments/${commentId}/vote`,
			{ method: "PUT", authenticated: true, body: { useful } },
		),
	removeVote: (commentId: number) =>
		apiRequest<{ useful: number; notUseful: number; currentVote: null }>(
			`/comments/${commentId}/vote`,
			{ method: "DELETE", authenticated: true },
		),
}
