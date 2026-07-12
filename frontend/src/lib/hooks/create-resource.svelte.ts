export function createResource<T>(loader: () => Promise<T>) {
	let data = $state<T | null>(null)
	let loading = $state(true)
	let error = $state<string | null>(null)
	async function load() {
		loading = true
		error = null
		try {
			data = await loader()
		} catch (cause) {
			error =
				cause instanceof Error ? cause.message : "Ocurrió un error inesperado."
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
		load,
		reload: load,
	}
}
