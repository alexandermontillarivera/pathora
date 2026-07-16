import type { ApiCareer } from "@lib/api/api-types"
import { catalogService } from "@lib/services/catalog-service"

let catalogPromise: Promise<ApiCareer[]> | null = null

function normalize(value: string) {
	return value
		.normalize("NFD")
		.replace(/[\u0300-\u036f]/g, "")
		.toLowerCase()
}

function words(value: string) {
	const ignored = new Set([
		"para",
		"como",
		"cual",
		"cuales",
		"quiero",
		"sobre",
		"donde",
		"puedo",
		"carrera",
		"carreras",
		"estudiar",
		"estudio",
		"gustaria",
		"recomienda",
		"gustaría",
	])
	return normalize(value)
		.split(/[^a-z0-9]+/)
		.filter((word) => word.length > 2 && !ignored.has(word))
}

export async function getCareerCatalog() {
	if (!catalogPromise) {
		catalogPromise = (async () => {
			const careers: ApiCareer[] = []
			let page = 1
			let hasNext = true
			while (hasNext) {
				const response = await catalogService.careers({
					page,
					max: 100,
					status: "ACTIVE",
				})
				careers.push(...response.records)
				hasNext = response.hasNext
				page += 1
			}
			return careers
		})()
	}
	return catalogPromise
}

function relevance(career: ApiCareer, queryWords: string[]) {
	const name = normalize(career.name)
	const school = normalize(career.school.name)
	const searchable = normalize(
		`${career.name} ${career.school.name} ${career.description} ${career.heroSummary} ${career.overview}`,
	)
	return queryWords.reduce((score, word) => {
		if (name.includes(word)) return score + 8
		if (school.includes(word)) return score + 4
		return searchable.includes(word) ? score + 1 : score
	}, career.rating / 10)
}

export async function buildCatalogContext(question: string) {
	const catalog = await getCareerCatalog()
	const queryWords = words(question)
	const relevant = catalog
		.toSorted((a, b) => relevance(b, queryWords) - relevance(a, queryWords))
		.slice(0, 7)
	const namedCareer = catalog.find((career) =>
		normalize(question).includes(normalize(career.name)),
	)
	let pensum = ""
	if (namedCareer) {
		try {
			const plan = await catalogService.pensum(namedCareer.id)
			pensum = `\nPENSUM DE ${namedCareer.name}:\n${plan.terms
				.map(
					(term) =>
						`Periodo ${term.number}: ${term.subjects
							.map((subject) => {
								const prerequisites = subject.prerequisites.length
									? ` (prerrequisitos: ${subject.prerequisites.map((item) => item.name).join(", ")})`
									: ""
								return `${subject.name}${prerequisites}`
							})
							.join("; ")}`,
				)
				.join("\n")}`
		} catch {
			pensum = ""
		}
	}

	const index = catalog
		.map(
			(career) =>
				`#${career.id} ${career.name} | ${career.school.name} | ${career.studyMode} | ${career.durationTerms} cuatrimestres | ${career.rating.toFixed(1)}/5`,
		)
		.join("\n")
	const details = relevant
		.map(
			(career) =>
				`#${career.id} ${career.name}: ${career.heroSummary} Perfil: ${career.professionalProfile} Resultados: ${career.outcomes.join("; ")}`,
		)
		.join("\n")

	return `CATÁLOGO DISPONIBLE:\n${index}\n\nDETALLES MÁS RELEVANTES:\n${details}${pensum}`
}

export async function careersMentionedIn(text: string) {
	const normalizedText = normalize(text)
	return (await getCareerCatalog())
		.filter((career) => normalizedText.includes(normalize(career.name)))
		.slice(0, 4)
}
