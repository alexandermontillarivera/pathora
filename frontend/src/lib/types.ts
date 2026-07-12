export type View = "discover" | "schools" | "community" | "career" | "profile"

export interface School {
	id: number
	name: string
	image: string
	description: string
	programs: number
	accent: string
}

export interface Career {
	id: number
	code: string
	name: string
	school: string
	image: string
	description: string
	heroSummary: string
	overview: string
	professionalProfile: string
	outcomes: string[]
	terms: number
	mode: "Presencial" | "Virtual" | "Mixta"
	rating: number
	reviews: number
	accent: string
	tags: string[]
}

export interface Subject {
	code: string
	name: string
	credits: number
	prerequisiteCodes: string[]
}
