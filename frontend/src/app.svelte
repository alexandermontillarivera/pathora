<script lang="ts">
	import { RouterView } from "@dvcol/svelte-simple-router/components"
	import RouterContext from '@dvcol/svelte-simple-router/components/context'
	import AppHeader from "@components/layout/app-header.svelte"
	import AppFooter from "@components/layout/app-footer.svelte"
	import AuthModal from "@components/auth/auth-modal.svelte"
	import { router } from "@lib/router"
	import { authModalOpen, authModalMode, closeAuthModal } from "@stores/ui"
	import { initializeAuth } from '@stores/auth'
	import { onMount } from 'svelte'
	onMount(initializeAuth)
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
</RouterContext>
