import { createInfiniteResource } from "@lib/hooks/create-infinite-resource.svelte"
import { mapCareer, mapSchool } from "@lib/services/catalog-mappers"
import { catalogService } from "@lib/services/catalog-service"
import type { School } from "@lib/types"
import { onMount } from "svelte"
import { replaceRoute } from "@lib/router"

const wait = (milliseconds: number) =>
	new Promise((resolve) => setTimeout(resolve, milliseconds))

export function useSearch() {
	const initialQuery = new URLSearchParams(location.search).get("q") ?? ""
	const filters = $state({
		query: initialQuery,
		appliedQuery: initialQuery,
		selectedSchools: [] as string[],
		modes: [] as string[],
		minimumRating: 0,
		sort: "relevance",
		mobileFilters: false,
	})
	let schools = $state<School[]>([])
	let pageLoading = $state(true)

	const resource = createInfiniteResource(async (page) => {
		const response = await catalogService.careers({
			page,
			max: 12,
			order: "ASC",
			name: filters.appliedQuery || undefined,
			status: "ACTIVE",
		})
		return { ...response, records: response.records.map(mapCareer) }
	})

	let results = $derived(
		resource.records
			.filter(
				(career) =>
					(!filters.selectedSchools.length ||
						filters.selectedSchools.includes(career.school)) &&
					(!filters.modes.length || filters.modes.includes(career.mode)) &&
					career.rating >= filters.minimumRating,
			)
			.toSorted((a, b) =>
				filters.sort === "rating"
					? b.rating - a.rating
					: filters.sort === "name"
						? a.name.localeCompare(b.name)
						: 0,
			),
	)
	let hasLocalFilters = $derived(
		filters.selectedSchools.length > 0 ||
			filters.modes.length > 0 ||
			filters.minimumRating > 0,
	)
	let resultCount = $derived(
		hasLocalFilters ? results.length : resource.totalRecords,
	)

	onMount(async () => {
		try {
			const [, schoolPage] = await Promise.all([
				resource.loadMore(),
				catalogService.schools({ max: 100 }),
			])
			schools = schoolPage.records.map(mapSchool)
		} finally {
			pageLoading = false
		}
	})

	async function loadCompleteCatalog() {
		pageLoading = true
		try {
			while (resource.loading) await wait(30)
			while (resource.hasNext && !resource.error) await resource.loadMore()
		} finally {
			pageLoading = false
		}
	}

	async function toggleSchool(value: string) {
		filters.selectedSchools = toggle(filters.selectedSchools, value)
		await loadCompleteCatalog()
	}

	async function toggleMode(value: string) {
		filters.modes = toggle(filters.modes, value)
		await loadCompleteCatalog()
	}

	async function setMinimumRating(value: number) {
		filters.minimumRating = value
		await loadCompleteCatalog()
	}

	async function submit() {
		pageLoading = true
		const query = filters.query.trim()
		filters.appliedQuery = query
		await replaceRoute(query ? `/search?q=${encodeURIComponent(query)}` : "/search")
		try {
			await resource.reload()
			if (hasLocalFilters) await loadCompleteCatalog()
		} finally {
			pageLoading = false
		}
	}

	async function clear() {
		pageLoading = true
		Object.assign(filters, {
			query: "",
			appliedQuery: "",
			selectedSchools: [],
			modes: [],
			minimumRating: 0,
		})
		try {
			await replaceRoute("/search")
			await resource.reload()
		} finally {
			pageLoading = false
		}
	}

	function toggle(list: string[], value: string) {
		return list.includes(value)
			? list.filter((item) => item !== value)
			: [...list, value]
	}

	return {
		filters,
		resource,
		submit,
		clear,
		toggleSchool,
		toggleMode,
		setMinimumRating,
		get schools() {
			return schools
		},
		get pageLoading() {
			return pageLoading
		},
		get results() {
			return results
		},
		get resultCount() {
			return resultCount
		},
	}
}
