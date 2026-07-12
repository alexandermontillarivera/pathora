import { createInfiniteResource } from "@lib/hooks/create-infinite-resource.svelte"
import { notificationService } from "@lib/services/notification-service"
import { onMount } from "svelte"

export type ActivityFilter = "ALL" | "REPLY" | "LOGIN"

export function useActivityPage() {
	let filter = $state<ActivityFilter>("ALL")
	let pageLoading = $state(true)
	const resource = createInfiniteResource((page) => notificationService.list(page, 15))
	let visible = $derived(resource.records.filter((item) => filter === "ALL" || item.type === filter))

	onMount(async () => { try { await resource.loadMore() } finally { pageLoading = false } })
	async function markAll() { await notificationService.readAll(); await resource.reload() }
	async function read(id: number) { await notificationService.read(id); await resource.reload() }

	return {
		resource, markAll, read,
		get filter() { return filter },
		set filter(value: ActivityFilter) { filter = value },
		get pageLoading() { return pageLoading },
		get visible() { return visible },
	}
}
