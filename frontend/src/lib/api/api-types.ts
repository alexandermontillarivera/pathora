export type PageResponse<T> = {
	records: T[]
	totalPages: number
	totalRecords: number
	page: number
	max: number
	order: "ASC" | "DESC"
	hasNext: boolean
	hasPrevious: boolean
}
export type ApiUser = {
	id: number
	firstName: string
	lastName: string
	email?: string
	description: string
	country?: string
	avatarSeed?: string
	createdAt: string
	comments?: number
	ratings?: number
}
export type AuthResponse = {
	accessToken: string
	refreshToken: string
	tokenType: string
	expiresAt: string
	refreshExpiresAt: string
	user: ApiUser
}
export type ApiSchool = {
	id: number
	name: string
	imageUrl: string
	description: string
	programs: number
}
export type ApiCareer = {
	id: number
	code: string
	name: string
	imageUrl: string
	description: string
	heroSummary: string
	overview: string
	professionalProfile: string
	outcomes: string[]
	school: { id: number; name: string }
	durationTerms: number
	durationYears: number
	studyMode: "ON_CAMPUS" | "ONLINE" | "HYBRID"
	status: "ACTIVE" | "INACTIVE"
	rating: number
	reviews: number
}
export type ApiPrerequisite = { id: number; code: string; name: string }
export type ApiPensum = {
	id: number
	version: string
	description: string
	effectiveFrom: number
	active: boolean
	totalCredits: number
	terms: {
		number: number
		subjects: {
			id: number
			code: string
			name: string
			description: string
			credits: number
			weeklyHours: number
			mandatory: boolean
			prerequisites: ApiPrerequisite[]
		}[]
	}[]
}
export type ApiComment = {
	id: number
	parentId: number | null
	content: unknown
	author: { id: number; firstName: string; lastName: string; avatarSeed?: string }
	career: { id: number; name: string }
	useful: number
	notUseful: number
	currentVote: boolean | null
	replies: ApiComment[]
	createdAt: string
	updatedAt: string
}
export type ApiRating = {
	id: number
	value: number
	user: { id: number; firstName: string; lastName: string; avatarSeed?: string }
	career: { id: number; name: string }
	createdAt: string
	updatedAt: string
}
export type RatingSummary = {
	count: number
	average: number
	distribution: Record<string, number>
}
export type ApiNotification = {
	id: number
	type: string
	title: string
	detail: string
	targetUrl: string | null
	read: boolean
	createdAt: string
}
export type SavedCareer = { id: number; career: ApiCareer; createdAt: string }
