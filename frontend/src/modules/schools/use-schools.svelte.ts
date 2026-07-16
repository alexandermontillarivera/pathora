import { createInfiniteResource } from "@lib/hooks/create-infinite-resource.svelte"
import { catalogService } from "@lib/services/catalog-service"
import { mapSchool } from "@lib/services/catalog-mappers"
import { onMount } from "svelte"

export function useSchoolsPage() {
	const resource = createInfiniteResource(async (page) => {
		const result = await catalogService.schools({ page, max: 6, order: "ASC" })
		return { ...result, records: result.records.map(mapSchool) }
	})
	let pageLoading = $state(true)
	onMount(async () => {
		try {
			await resource.loadMore()
		} finally {
			pageLoading = false
		}
	})
	return {
		resource,
		get pageLoading() {
			return pageLoading
		},
	}
}
