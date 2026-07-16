import { errorMessage, errorStatus } from "@lib/utils/app-error"

export function createResource<T>(loader: () => Promise<T>) {
	let data = $state<T | null>(null)
	let loading = $state(true)
	let error = $state<string | null>(null)
	let status = $state<number | undefined>()
	async function load() {
		loading = true
		error = null
		status = undefined
		try {
			data = await loader()
		} catch (cause) {
			error = errorMessage(cause)
			status = errorStatus(cause)
		} finally {
			loading = false
		}
	}
	return {
		get data() {
			return data
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
		load,
		reload: load,
	}
}
