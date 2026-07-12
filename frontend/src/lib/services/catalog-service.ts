import { apiRequest, queryString } from "@lib/api/http-client"
import type {
	ApiCareer,
	ApiPensum,
	ApiSchool,
	PageResponse,
} from "@lib/api/api-types"

export const catalogService = {
	schools: (params: { page?: number; max?: number; order?: string } = {}) =>
		apiRequest<PageResponse<ApiSchool>>(`/schools${queryString(params)}`),
	school: (id: number) => apiRequest<ApiSchool>(`/schools/${id}`),
	careers: (
		params: {
			page?: number
			max?: number
			order?: string
			name?: string
			schoolId?: number
			studyMode?: "ON_CAMPUS" | "ONLINE" | "HYBRID"
			status?: "ACTIVE" | "INACTIVE"
		} = {},
	) => apiRequest<PageResponse<ApiCareer>>(`/careers${queryString(params)}`),
	career: (id: number) => apiRequest<ApiCareer>(`/careers/${id}`),
	pensum: (careerId: number) =>
		apiRequest<ApiPensum>(`/pensums/career/${careerId}`),
}
