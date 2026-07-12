import { writable } from "svelte/store"

export const authModalOpen = writable(false)
export type AuthModalMode = "login" | "register" | "forgot"
export const authModalMode = writable<AuthModalMode>("login")
export const openAuthModal = (mode: AuthModalMode | Event = "login") => {
	authModalMode.set(typeof mode === "string" ? mode : "login")
	authModalOpen.set(true)
}
export const closeAuthModal = () => authModalOpen.set(false)
