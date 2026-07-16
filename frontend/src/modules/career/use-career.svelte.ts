import type { ApiComment, ApiPensum, RatingSummary } from "@lib/api/api-types"
import { router } from "@lib/router"
import { catalogService } from "@lib/services/catalog-service"
import { mapCareer } from "@lib/services/catalog-mappers"
import { commentService } from "@lib/services/comment-service"
import { ratingService } from "@lib/services/rating-service"
import { savedService } from "@lib/services/saved-service"
import { savedCareerIds, setCareerSaved } from "@stores/saved-careers"
import type { Career, Subject } from "@lib/types"
import { onDestroy, onMount } from "svelte"
import { errorMessage, errorStatus } from "@lib/utils/app-error"
import { tokenStorage } from "@lib/api/http-client"

export type CareerTab = "overview" | "pensum" | "community"

export function useCareer() {
	const rawId = String(router.location?.params.id ?? "").split("?")[0]
	const id = Number(rawId)
	const browserQuery = new URLSearchParams(window.location.search)
	const targetCommentId =
		Number(router.location?.query.comment ?? browserQuery.get("comment")) || null
	const initialTab: CareerTab =
		(router.location?.query.tab ?? browserQuery.get("tab")) === "community"
			? "community"
			: "overview"
	let career = $state<Career>({
		id,
		code: "",
		name: "",
		school: "",
		image: "",
		description: "",
		heroSummary: "",
		overview: "",
		professionalProfile: "",
		outcomes: [],
		terms: 0,
		mode: "Mixta",
		rating: 0,
		reviews: 0,
		accent: "#c9ff56",
		tags: [],
	})
	let apiPensum = $state<ApiPensum | null>(null)
	let rating = $state<RatingSummary | null>(null)
	let loading = $state(true)
	let loadError = $state("")
	let loadErrorStatus = $state<number | undefined>()
	let activeTerm = $state(0)
	let tab = $state<CareerTab>(initialTab)
	let saved = $state(false)
	let composing = $state(false)
	let myRating = $state(0)
	let myRatingId = $state<number | null>(null)
	let ratingPending = $state<number | null>(null)
	let ratingError = $state("")
	let careerComments = $state<ReturnType<typeof mapComment>[]>([])
	const unsubscribeSaved = savedCareerIds.subscribe((ids) => {
		saved = ids.has(id)
	})
	onDestroy(unsubscribeSaved)
	let pensum = $derived<Subject[][]>(
		apiPensum?.terms.map((term) =>
			term.subjects.map((subject) => ({
				code: subject.code,
				name: subject.name,
				credits: subject.credits,
				prerequisiteCodes: subject.prerequisites.map((item) => item.code),
			})),
		) ?? [],
	)
	let allSubjects = $derived(pensum.flat())

	function mapComment(item: ApiComment) {
		return {
			id: item.id,
			userId: item.author.id,
			name: `${item.author.firstName} ${item.author.lastName}`,
			initials: `${item.author.firstName[0] ?? ""}${item.author.lastName[0] ?? ""}`,
			avatarSeed: item.author.avatarSeed,
			time: new Intl.DateTimeFormat("es", { dateStyle: "medium" }).format(
				new Date(item.createdAt),
			),
			content: item.content,
			rating: 5,
			useful: item.useful,
			notUseful: item.notUseful,
			currentVote: item.currentVote,
			repliesData: item.replies,
		}
	}

	function prerequisiteName(code: string) {
		return allSubjects.find((subject) => subject.code === code)?.name ?? code
	}

	onMount(async () => {
		try {
			const [careerResponse, pensumResponse, commentsResponse, ratingResponse, personalRating] =
				await Promise.all([
					catalogService.career(id),
					catalogService.pensum(id),
					commentService.list(id, 1, 20),
					ratingService.summary(id),
					tokenStorage.get() ? ratingService.mine(id) : Promise.resolve(null),
				])
			career = mapCareer(careerResponse)
			apiPensum = pensumResponse
			careerComments = commentsResponse.records.map(mapComment)
			if (
				targetCommentId &&
				!careerComments.some((comment) => comment.id === targetCommentId)
			) {
				const target = await commentService.find(targetCommentId)
				if (target.career.id === id)
					careerComments = [...careerComments, mapComment(target)]
			}
			rating = ratingResponse
			myRating = personalRating?.value ?? 0
			myRatingId = personalRating?.id ?? null
		} catch (cause) {
			loadError = errorMessage(cause)
			loadErrorStatus = errorStatus(cause)
		} finally {
			loading = false
		}
	})

	async function addComment(html: string) {
		careerComments = [
			mapComment(await commentService.create(id, html)),
			...careerComments,
		]
		composing = false
	}

	async function toggleSaved() {
		const nextSaved = !saved
		saved = nextSaved
		setCareerSaved(id, nextSaved)
		try {
			nextSaved ? await savedService.add(id) : await savedService.remove(id)
		} catch (cause) {
			saved = !nextSaved
			setCareerSaved(id, !nextSaved)
			throw cause
		}
	}

	async function rate(value: number) {
		if (ratingPending !== null) return
		const previousRating = myRating
		ratingPending = value
		ratingError = ""
		myRating = value
		try {
			const updated = myRatingId
				? await ratingService.update(myRatingId, value)
				: await ratingService.create(id, value)
			myRatingId = updated.id
			rating = await ratingService.summary(id)
		} catch (cause) {
			myRating = previousRating
			ratingError =
				cause instanceof Error
					? cause.message
					: "No pudimos guardar tu valoración. Inténtalo nuevamente."
		} finally {
			ratingPending = null
		}
	}

	return {
		prerequisiteName,
		targetCommentId,
		get allSubjects() { return allSubjects },
		addComment,
		toggleSaved,
		rate,
		get career() { return career },
		get apiPensum() { return apiPensum },
		get rating() { return rating },
		get loading() { return loading },
		get loadError() { return loadError },
		get loadErrorStatus() { return loadErrorStatus },
		get pensum() { return pensum },
		get activeTerm() { return activeTerm },
		set activeTerm(value: number) { activeTerm = value },
		get tab() { return tab },
		set tab(value: CareerTab) { tab = value },
		get saved() { return saved },
		get composing() { return composing },
		set composing(value: boolean) { composing = value },
		get myRating() { return myRating },
		get ratingPending() { return ratingPending },
		get ratingError() { return ratingError },
		get careerComments() { return careerComments },
	}
}
