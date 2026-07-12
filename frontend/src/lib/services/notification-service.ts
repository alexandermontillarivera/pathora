import type { ApiNotification, PageResponse } from "@lib/api/api-types"
import { apiRequest, queryString } from "@lib/api/http-client"

export const notificationService = {
	list: (page = 1, max = 20) =>
		apiRequest<PageResponse<ApiNotification>>(
			`/me/notifications${queryString({ page, max, order: "DESC" })}`,
			{ authenticated: true },
		),
	summary: () =>
		apiRequest<{ unread: number }>("/me/notifications/summary", {
			authenticated: true,
		}),
	read: (id: number) =>
		apiRequest<void>(`/me/notifications/${id}/read`, {
			method: "PUT",
			authenticated: true,
		}),
	readAll: () =>
		apiRequest<void>("/me/notifications/read-all", {
			method: "PUT",
			authenticated: true,
		}),
}
