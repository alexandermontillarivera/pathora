import { mapCareer, mapSchool } from "@lib/services/catalog-mappers"
import { createResource } from "@lib/hooks/create-resource.svelte"
import { catalogService } from "@lib/services/catalog-service"
import { router } from "@lib/router"
import { onMount } from "svelte"

export function useSchoolDetailPage() {
	const id = Number(router.location?.params.id)
	const resource = createResource(async () => {
		const [school, programs] = await Promise.all([
			catalogService.school(id),
			catalogService.careers({ schoolId: id, max: 100, status: "ACTIVE" }),
		])
		return {
			school: mapSchool(school),
			programs: programs.records.map(mapCareer),
		}
	})
	let school = $derived(
		resource.data?.school ?? {
			id,
			name: "",
			image: "",
			description: "",
			programs: 0,
			accent: "#c9ff56",
		},
	)
	let programs = $derived(resource.data?.programs ?? [])
	onMount(() => resource.load())
	return {
		resource,
		get school() {
			return school
		},
		get programs() {
			return programs
		},
	}
}
