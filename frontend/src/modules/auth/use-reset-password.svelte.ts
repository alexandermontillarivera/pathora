import { authService } from "@lib/services/auth-service"

export function useResetPasswordPage() {
	const token = new URLSearchParams(window.location.search).get("token") ?? ""
	const form = $state({ password: "", confirmation: "" })
	let loading = $state(false)
	let complete = $state(false)
	let error = $state(token ? "" : "El enlace no contiene un token válido.")

	async function submit() {
		if (form.password !== form.confirmation) { error = "Las contraseñas no coinciden."; return }
		loading = true; error = ""
		try { await authService.resetPassword(token, form.password); complete = true }
		catch (cause) { error = cause instanceof Error ? cause.message : "No pudimos cambiar la contraseña." }
		finally { loading = false }
	}
	return { token, form, submit, get loading(){return loading}, get complete(){return complete}, get error(){return error} }
}
