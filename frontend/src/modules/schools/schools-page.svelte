<script lang="ts">
	import InfiniteSentinel from "@components/ui/infinite-sentinel.svelte"
	import PageLoader from "@components/ui/page-loader.svelte"
	import PageState from "@components/ui/page-state.svelte"
	import { link } from "@dvcol/svelte-simple-router/action"
	import { markdownSummary } from "@lib/utils/markdown"
	import { useSchoolsPage } from "./use-schools.svelte"
	const page=useSchoolsPage()
</script>

<svelte:head><title>Escuelas — Pathora</title></svelte:head>

<main class="page-shell page">
	{#if page.pageLoading}<PageLoader />
	{:else if page.resource.error && !page.resource.records.length}<PageState title="No pudimos cargar las escuelas." message={page.resource.error} onRetry={page.resource.reload} />
	{:else}
	<header>
		<span class="kicker">15 espacios de aprendizaje</span>
		<h1>Escuelas con una<br />mirada propia.</h1>
		<p>
			Cada escuela reúne disciplinas, personas y laboratorios alrededor de
			preguntas que vale la pena explorar.
		</p>
	</header>
	<div class="grid">
		{#each page.resource.records as school, i}<article
				style={`--accent:${school.accent}`}
			>
				<div class="number">0{i + 1}</div>
				<div class="image"><img src={school.image} alt="" /></div>
				<div class="copy">
					<span>{school.programs} programas</span>
					<h2>{school.name}</h2>
					<p>{markdownSummary(school.description)}</p>
					<a href={`/schools/${school.id}`} use:link
						>Explorar escuela <span>↗</span></a
					>
				</div>
			</article>{/each}
	</div>
	{#if page.resource.error}<PageState compact title="La siguiente página no pudo cargarse." message={page.resource.error} onRetry={page.resource.loadMore} />{/if}
	<InfiniteSentinel
		disabled={!page.resource.hasNext || page.resource.loading}
		loading={page.resource.loading && !page.pageLoading}
		label="Cargando más escuelas"
		onVisible={page.resource.loadMore}
	/>
	{/if}
</main>

<style>
	.page {
		padding-top: 6rem;
		padding-bottom: 9rem;
	}
	.kicker {
		font-size: 0.7rem;
		text-transform: uppercase;
		letter-spacing: 0.15em;
		color: var(--muted);
		font-weight: 700;
	}
	header h1 {
		font-family: var(--display);
		font-size: clamp(4rem, 8vw, 8rem);
		line-height: 0.87;
		letter-spacing: -0.07em;
		margin: 1.5rem 0 2rem;
	}
	header p {
		max-width: 600px;
		color: var(--muted);
		font-size: 1.1rem;
		line-height: 1.65;
	}
	.grid {
		margin-top: 7rem;
		border-top: 1px solid var(--line);
	}
	article {
		display: grid;
		grid-template-columns: 80px 320px 1fr;
		gap: 2rem;
		padding: 2rem 0;
		border-bottom: 1px solid var(--line);
		align-items: center;
	}
	.number {
		align-self: start;
		font-family: var(--display);
		font-size: 0.8rem;
		color: var(--muted);
	}
	.image {
		height: 210px;
		border-radius: 18px;
		overflow: hidden;
		background: var(--accent);
	}
	.image img {
		width: 100%;
		height: 100%;
		object-fit: cover;
		transition: transform 0.6s;
	}
	.copy {
		max-width: 620px;
		padding-left: 2rem;
	}
	.copy > span {
		font-size: 0.68rem;
		letter-spacing: 0.1em;
		text-transform: uppercase;
		color: var(--muted);
	}
	h2 {
		font-family: var(--display);
		font-size: 2.2rem;
		line-height: 1;
		letter-spacing: -0.04em;
		margin: 0.7rem 0;
	}
	.copy p {
		color: var(--muted);
		line-height: 1.6;
	}
	.copy a {
		border: 0;
		background: none;
		padding: 0;
		font: inherit;
		font-size: 0.82rem;
		font-weight: 700;
		cursor: pointer;
		color: inherit;
		text-decoration: none;
	}
	.copy a span {
		display: inline-block;
		margin-left: 0.3rem;
		transition: transform 0.2s;
	}
	article:hover img {
		transform: scale(1.04);
	}
	article:hover a span {
		transform: translate(3px, -3px);
	}

	@media (max-width: 800px) {
		article {
			grid-template-columns: 40px 1fr;
		}
		.image {
			height: 180px;
		}
		.copy {
			grid-column: 2;
			padding: 0;
		}
	}
	@media (max-width: 520px) {
		article {
			grid-template-columns: 1fr;
		}
		.number {
			display: none;
		}
		.copy {
			grid-column: 1;
		}
		.page {
			padding-top: 4rem;
		}
		.grid {
			margin-top: 4rem;
		}
	}
</style>
