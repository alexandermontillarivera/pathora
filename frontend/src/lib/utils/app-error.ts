import { ApiError } from "@lib/api/http-client"

export function errorStatus(cause: unknown) {
	return cause instanceof ApiError ? cause.status : undefined
}

export function errorMessage(cause: unknown) {
	if (cause instanceof TypeError || (cause instanceof Error && cause.message === "Failed to fetch")) {
		return "No pudimos conectar con Pathora. Comprueba tu conexión e inténtalo nuevamente."
	}
	if (cause instanceof ApiError && cause.status >= 500) {
		return "El servicio no está disponible en este momento. Inténtalo nuevamente en unos instantes."
	}
	return cause instanceof Error
		? cause.message
		: "Ocurrió un error inesperado. Inténtalo nuevamente."
}
