<script lang="ts">
	import { RouterView } from "@dvcol/svelte-simple-router/components"
	import RouterContext from '@dvcol/svelte-simple-router/components/context'
	import AppHeader from "@components/layout/app-header.svelte"
	import AppFooter from "@components/layout/app-footer.svelte"
	import AuthModal from "@components/auth/auth-modal.svelte"
	import LocalAiAssistant from "@components/ai/local-ai-assistant.svelte"
	import { router } from "@lib/router"
	import { authModalOpen, authModalMode, closeAuthModal } from "@stores/ui"
	import { currentUser, initializeAuth } from '@stores/auth'
	import { clearSavedCareerIds, loadSavedCareerIds } from '@stores/saved-careers'
	import { setLocalAiUser } from '@stores/local-ai'
	import { onMount } from 'svelte'
	onMount(initializeAuth)
	let loadedSavedForUser = $state<number | null>(null)
	let configuredAiForUser = $state<number | null>(null)
	$effect(() => {
		const userId = $currentUser?.id ?? null
		if (configuredAiForUser !== userId) {
			configuredAiForUser = userId
			setLocalAiUser(userId)
		}
		if (userId && loadedSavedForUser !== userId) {
			loadedSavedForUser = userId
			loadSavedCareerIds().catch(clearSavedCareerIds)
		} else if (!userId && loadedSavedForUser !== null) {
			loadedSavedForUser = null
			clearSavedCareerIds()
		}
	})
	$effect(() => {
		const path = router.location?.path
		if (path) window.scrollTo({ top: 0, left: 0, behavior: 'auto' })
	})

</script>

<svelte:head>
	<meta
		name="description"
		content="Explora carreras, escuelas y experiencias universitarias."
	/>
</svelte:head>
<RouterContext {router}>
	<AppHeader />
	<RouterView />
	<AppFooter />
	{#if $authModalOpen}<AuthModal initialMode={$authModalMode} onClose={closeAuthModal} />{/if}
	{#if $currentUser}<LocalAiAssistant />{/if}
</RouterContext>
