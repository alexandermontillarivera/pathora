<script lang="ts">
	import InfiniteSentinel from "@components/ui/infinite-sentinel.svelte"
	import PageLoader from "@components/ui/page-loader.svelte"
	import PageState from "@components/ui/page-state.svelte"
	import Icon from "@components/ui/icon.svelte"
	import { useActivityPage } from "./use-activity.svelte"
	const page=useActivityPage()
</script>

<svelte:head><title>Tu actividad — Pathora</title></svelte:head>
<main class="page-shell page">
	{#if page.pageLoading}<PageLoader />
	{:else if page.resource.error && !page.resource.records.length}<PageState title="No pudimos cargar tu actividad." message={page.resource.error} onRetry={page.resource.reload} />
	{:else}
	<header>
		<div>
			<span>Centro personal</span>
			<h1>Tu actividad.</h1>
			<p>Respuestas y accesos a tu cuenta, reunidos en un solo lugar.</p>
		</div>
		<button onclick={page.markAll}
			><Icon name="check" size={17} />Marcar todo como leído</button
		>
	</header>
	<nav>
		<button class:active={page.filter === "ALL"} onclick={() => (page.filter = "ALL")}
			>Todo</button
		><button
			class:active={page.filter === "REPLY"}
			onclick={() => (page.filter = "REPLY")}>Conversaciones</button
		><button
			class:active={page.filter === "LOGIN"}
			onclick={() => (page.filter = "LOGIN")}>Seguridad</button
		>
	</nav>
	<section>
		{#each page.visible as item}<button
				class:unread={!item.read}
				onclick={() => page.read(item.id)}
				><i
					><Icon
						name={item.type === "REPLY" ? "message" : "user"}
						size={19}
					/></i
				>
				<div>
					<strong>{item.title}</strong>
					<p>{item.detail}</p>
					<time
						>{new Intl.DateTimeFormat("es", {
							dateStyle: "long",
							timeStyle: "short",
						}).format(new Date(item.createdAt))}</time
					>
				</div></button
			>{/each}{#if page.resource.error}<PageState compact title="No pudimos cargar más actividad." message={page.resource.error} onRetry={page.resource.loadMore} />{:else if !page.resource.loading && !page.visible.length}<div
				class="empty"
			>
				<h2>Todo está al día.</h2>
				<p>No hay actividad para este filtro.</p>
			</div>{/if}<InfiniteSentinel
			disabled={!page.resource.hasNext || page.resource.loading}
			loading={page.resource.loading && !page.pageLoading}
			label="Cargando más actividad"
			onVisible={page.resource.loadMore}
		/>
	</section>
	{/if}
</main>

<style>
	.page {
		padding-top: 6rem;
		padding-bottom: 10rem;
		max-width: 1050px;
	}
	header {
		display: flex;
		align-items: end;
		justify-content: space-between;
		gap: 3rem;
		padding-bottom: 3rem;
		border-bottom: 1px solid var(--line);
	}
	header span {
		font-size: 0.68rem;
		text-transform: uppercase;
		letter-spacing: 0.13em;
		color: var(--muted);
	}
	h1 {
		font-family: var(--display);
		font-size: clamp(4rem, 8vw, 7rem);
		line-height: 0.88;
		letter-spacing: -0.07em;
		margin: 1rem 0;
	}
	header p {
		color: var(--muted);
	}
	header button {
		border: 1px solid var(--line);
		border-radius: 12px;
		background: white;
		padding: 0.75rem;
		display: flex;
		gap: 0.4rem;
		font: inherit;
	}
	nav {
		display: flex;
		gap: 0.6rem;
		margin: 2rem 0 4rem;
	}
	nav button {
		border: 1px solid var(--line);
		border-radius: 99px;
		padding: 0.6rem 1rem;
		background: transparent;
		font: inherit;
		color: var(--muted);
	}
	nav button.active {
		background: var(--ink);
		color: white;
	}
	section > button {
		width: 100%;
		padding: 1.3rem;
		border: 0;
		border-bottom: 1px solid var(--line);
		background: transparent;
		display: grid;
		grid-template-columns: 44px 1fr;
		gap: 1rem;
		text-align: left;
		font: inherit;
	}
	.unread {
		background: rgba(202, 255, 79, 0.09);
	}
	section i {
		width: 44px;
		height: 44px;
		border-radius: 13px;
		background: var(--surface-2);
		display: grid;
		place-items: center;
	}
	section p {
		color: var(--muted);
		margin: 0.35rem 0;
	}
	time {
		font-size: 0.65rem;
		color: var(--muted);
	}
	.empty {
		text-align: center;
		padding: 5rem;
		border: 1px dashed var(--line);
		border-radius: 22px;
	}
	.empty h2 {
		font-family: var(--display);
		font-size: 2rem;
	}
	@media (max-width: 650px) {
		header {
			align-items: start;
			flex-direction: column;
		}
	}
</style>
