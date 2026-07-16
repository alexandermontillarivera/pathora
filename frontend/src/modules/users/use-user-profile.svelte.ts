import { commentService } from "@lib/services/comment-service"
import { catalogService } from "@lib/services/catalog-service"
import type { ApiComment, ApiUser } from "@lib/api/api-types"
import { ratingService } from "@lib/services/rating-service"
import { mapCareer } from "@lib/services/catalog-mappers"
import { userService } from "@lib/services/user-service"
import type { Career } from "@lib/types"
import { router } from "@lib/router"
import { onMount } from "svelte"

export type UserProfileTab = "activity" | "ratings"

export function useUserProfile() {
	const id = Number(router.location?.params.id)
	let tab = $state<UserProfileTab>("activity"),
		user = $state<ApiUser | null>(null),
		comments = $state<ApiComment[]>([]),
		reviewed = $state<Career[]>([]),
		loading = $state(true),
		error = $state("")
	function text(content: unknown) {
		if (content && typeof content === "object" && "html" in content) {
			const node = document.createElement("div")
			node.innerHTML = String((content as { html: string }).html)
			return node.textContent ?? ""
		}
		return typeof content === "string" ? content : "Comentario enriquecido"
	}
	onMount(async () => {
		try {
			const [u, c, r] = await Promise.all([
				userService.find(id),
				commentService.byUser(id),
				ratingService.byUser(id),
			])
			user = u
			comments = c.records
			reviewed = (
				await Promise.all(
					r.records.map((x) => catalogService.career(x.career.id)),
				)
			).map(mapCareer)
		} catch (cause) {
			error =
				cause instanceof Error ? cause.message : "No pudimos cargar el perfil."
		} finally {
			loading = false
		}
	})
	return {
		text,
		get tab() {
			return tab
		},
		set tab(v: UserProfileTab) {
			tab = v
		},
		get user() {
			return user
		},
		get comments() {
			return comments
		},
		get reviewed() {
			return reviewed
		},
		get loading() {
			return loading
		},
		get error() {
			return error
		},
	}
}
