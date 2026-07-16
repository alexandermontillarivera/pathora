import type { PageResponse } from "@lib/api/api-types"
import { errorMessage, errorStatus } from "@lib/utils/app-error"

export function createInfiniteResource<T>(
	loader: (page: number) => Promise<PageResponse<T>>,
) {
	let error = $state<string | null>(null)
	let status = $state<number | undefined>()
	let loading = $state(false)
	let records = $state<T[]>([])
	let hasNext = $state(true)
	let page = $state(0)
	let totalRecords = $state(0)
	let totalPages = $state(0)
	async function loadMore() {
		if (loading || !hasNext) return
		loading = true
		error = null
		status = undefined
		try {
			const next = await loader(page + 1)
			records = [...records, ...next.records]
			page = next.page
			hasNext = next.hasNext
			totalRecords = next.totalRecords
			totalPages = next.totalPages
		} catch (cause) {
			error = errorMessage(cause)
			status = errorStatus(cause)
		} finally {
			loading = false
		}
	}
	async function reload() {
		records = []
		page = 0
		hasNext = true
		totalRecords = 0
		totalPages = 0
		await loadMore()
	}
	return {
		get records() {
			return records
		},
		get page() {
			return page
		},
		get loading() {
			return loading
		},
		get error() {
			return error
		},
		get errorStatus() {
			return status
		},
		get hasNext() {
			return hasNext
		},
		get totalRecords() {
			return totalRecords
		},
		get totalPages() {
			return totalPages
		},
		loadMore,
		reload,
	}
}
