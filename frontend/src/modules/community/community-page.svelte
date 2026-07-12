<script lang="ts">
 import InfiniteSentinel from "@components/ui/infinite-sentinel.svelte";import CommentThread from "@components/comments/comment-thread.svelte";import PageLoader from "@components/ui/page-loader.svelte";import Icon from "@components/ui/icon.svelte";import{navigate}from"@lib/router";import{currentUser}from"@stores/auth";import{useCommunity}from"./use-community.svelte";const page=useCommunity();let{resource,stats,pageLoading}=$derived(page);const{text}=page
</script>

<svelte:head><title>Comunidad — Pathora</title></svelte:head>

<main class="page-shell page">
	{#if pageLoading}<PageLoader />{/if}
	<header>
		<span>Comunidad Pathora</span>
		<h1>Decisiones más humanas.</h1>
		<p>
			Experiencias honestas, preguntas útiles y perspectivas de estudiantes que
			ya están recorriendo el camino.
		</p>
		{#if $currentUser}<button onclick={() => navigate("/search")}
				>Comparte tu experiencia <Icon name="arrow" size={17} /></button
			>{/if}
	</header>
	<section class="pulse">
		<div>
			<strong>{stats?.averageRating || "—"}</strong><span
				>Valoración promedio</span
			>
		</div>
		<div>
			<strong>{stats?.comments ?? 0}</strong><span
				>Experiencias compartidas</span
			>
		</div>
		<div>
			<strong>{stats?.careers ?? 0}</strong><span>Carreras conversando</span>
		</div>
	</section>
	<section class="feed">
		<div class="feed-title">
			<span>Conversaciones recientes</span><button>Más útiles primero⌄</button>
		</div>
		{#each resource.records as item}
			<CommentThread
				comment={{
					id: item.id,
					userId: item.author.id,
					name: `${item.author.firstName} ${item.author.lastName}`,
					initials: `${item.author.firstName[0] ?? ""}${item.author.lastName[0] ?? ""}`,
					time: new Intl.DateTimeFormat("es", { dateStyle: "medium" }).format(
						new Date(item.createdAt),
					),
					text: text(item.content),
					rating: 5,
				}}
				career={item.career.name}
				useful={item.useful}
				repliesData={item.replies}
			/>
		{/each}
		{#if resource.error}<div class="load-error" role="alert">
				{resource.error}<button onclick={() => resource.loadMore()}
					>Reintentar</button
				>
			</div>{/if}
		<InfiniteSentinel
			disabled={!resource.hasNext || resource.loading}
			loading={resource.loading && !pageLoading}
			label="Cargando más conversaciones"
			onVisible={resource.loadMore}
		/>
	</section>
</main>

<style>
	.page {
		padding-top: 6rem;
		padding-bottom: 9rem;
	}
	header {
		max-width: 950px;
	}
	header > span,
	.feed-title > span {
		text-transform: uppercase;
		letter-spacing: 0.14em;
		font-size: 0.68rem;
		color: var(--muted);
		font-weight: 700;
	}
	h1 {
		font-family: var(--display);
		font-size: clamp(4.2rem, 9vw, 9rem);
		line-height: 0.86;
		letter-spacing: -0.075em;
		margin: 1.5rem 0 2rem;
	}
	header p {
		max-width: 650px;
		color: var(--muted);
		font-size: 1.1rem;
		line-height: 1.7;
	}
	header button {
		margin-top: 1rem;
		border: 0;
		border-radius: 12px;
		padding: 0.9rem 1rem;
		background: var(--ink);
		color: white;
		display: flex;
		align-items: center;
		gap: 0.5rem;
		font: inherit;
		cursor: pointer;
	}
	.pulse {
		margin: 6rem 0;
		display: grid;
		grid-template-columns: repeat(3, 1fr);
		border-top: 1px solid var(--line);
		border-bottom: 1px solid var(--line);
	}
	.pulse div {
		padding: 2.5rem 0;
		display: flex;
		flex-direction: column;
		border-right: 1px solid var(--line);
	}
	.pulse div + div {
		padding-left: 2.5rem;
	}
	.pulse div:last-child {
		border: 0;
	}
	.pulse strong {
		font-family: var(--display);
		font-size: 3.5rem;
		letter-spacing: -0.05em;
	}
	.pulse span {
		font-size: 0.75rem;
		color: var(--muted);
	}
	.feed {
		width: 100%;
		max-width: 1040px;
		margin-left: auto;
		margin-right: auto;
	}
	.feed-title {
		display: flex;
		justify-content: space-between;
		padding-bottom: 1.5rem;
		border-bottom: 1px solid var(--line);
	}
	.feed-title button {
		border: 0;
		background: none;
		font: inherit;
		font-size: 0.72rem;
		color: var(--muted);
	}

	.load-error {
		padding: 2rem;
		text-align: center;
		color: var(--muted);
	}
	.load-error button {
		margin-left: 0.6rem;
		border: 1px solid var(--line);
		border-radius: 8px;
		background: white;
		padding: 0.5rem;
		font: inherit;
	}
	@media (max-width: 650px) {
		.pulse {
			grid-template-columns: 1fr;
		}
		.pulse div,
		.pulse div + div {
			padding: 1.5rem 0;
			border-right: 0;
			border-bottom: 1px solid var(--line);
		}
	}
</style>
