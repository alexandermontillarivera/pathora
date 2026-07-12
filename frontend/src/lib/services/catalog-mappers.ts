import type { ApiCareer, ApiSchool } from "@lib/api/api-types"
import type { Career, School } from "@lib/types"

const accents = [
	"#c9ff56",
	"#ffc6a8",
	"#a9d5ff",
	"#ffd76b",
	"#9ee4bd",
	"#d5c7ff",
]

export function mapSchool(value: ApiSchool): School {
	return {
		id: value.id,
		name: value.name,
		image: value.imageUrl,
		description: value.description,
		programs: value.programs,
		accent: accents[(value.id - 1) % accents.length],
	}
}

export function mapCareer(value: ApiCareer): Career {
	return {
		id: value.id,
		code: value.code,
		name: value.name,
		school: value.school.name,
		image: value.imageUrl,
		description: value.description,
		heroSummary: value.heroSummary,
		overview: value.overview,
		professionalProfile: value.professionalProfile,
		outcomes: value.outcomes,
		terms: value.durationTerms,
		mode:
			value.studyMode === "ON_CAMPUS"
				? "Presencial"
				: value.studyMode === "ONLINE"
					? "Virtual"
					: "Mixta",
		rating: value.rating,
		reviews: value.reviews,
		accent: accents[(value.school.id - 1) % accents.length],
		tags: [],
	}
}
