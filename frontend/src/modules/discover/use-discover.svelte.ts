import { mapCareer, mapSchool } from "@lib/services/catalog-mappers"
import { catalogService } from "@lib/services/catalog-service"
import type { Career, School } from "@lib/types"
import { navigate } from "@lib/router"
import { errorMessage } from "@lib/utils/app-error"
import { onMount } from "svelte"

export function useDiscover() {
	const form = $state({ query: "", mode: "Todas" })
	let careers = $state<Career[]>([]),
		schools = $state<School[]>([])
	let loading = $state(true),
		loadError = $state(""),
		totalCareers = $state(0)
	const modes = ["Todas", "Presencial", "Virtual", "Mixta"]
	let featured = $derived(careers.toSorted((a, b) => b.rating - a.rating)[0])
	let filtered = $derived(
		careers.filter(
			(career) => form.mode === "Todas" || career.mode === form.mode,
		),
	)
	async function load() {
		loading = true
		loadError = ""
		try {
			const [careerPage, schoolPage] = await Promise.all([
				catalogService.careers({ page: 1, max: 6, status: "ACTIVE" }),
				catalogService.schools({ page: 1, max: 4 }),
			])
			careers = careerPage.records.map(mapCareer)
			totalCareers = careerPage.totalRecords
			schools = schoolPage.records.map(mapSchool)
		} catch (cause) {
			loadError = errorMessage(cause)
		} finally {
			loading = false
		}
	}
	onMount(load)
	function search() {
		navigate(
			form.query.trim()
				? `/search?q=${encodeURIComponent(form.query.trim())}`
				: "/search",
		)
	}
	return {
		form,
		modes,
		search,
		reload: load,
		get careers() {
			return careers
		},
		get schools() {
			return schools
		},
		get loading() {
			return loading
		},
		get loadError() {
			return loadError
		},
		get totalCareers() {
			return totalCareers
		},
		get featured() {
			return featured
		},
		get filtered() {
			return filtered
		},
	}
}
