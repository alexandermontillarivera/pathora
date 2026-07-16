import { savedService } from "@lib/services/saved-service"
import { writable } from "svelte/store"

export const savedCareerIds = writable<ReadonlySet<number>>(new Set())
let generation = 0

export async function loadSavedCareerIds() {
	const requestGeneration = ++generation
	const ids = new Set<number>()
	let page = 1
	let hasNext = true

	while (hasNext) {
		const response = await savedService.list(page, 100)
		response.records.forEach((item) => ids.add(item.career.id))
		hasNext = response.hasNext
		page += 1
	}

	if (requestGeneration === generation) savedCareerIds.set(ids)
}

export function setCareerSaved(careerId: number, saved: boolean) {
	savedCareerIds.update((current) => {
		const next = new Set(current)
		saved ? next.add(careerId) : next.delete(careerId)
		return next
	})
}

export function clearSavedCareerIds() {
	generation += 1
	savedCareerIds.set(new Set())
}
