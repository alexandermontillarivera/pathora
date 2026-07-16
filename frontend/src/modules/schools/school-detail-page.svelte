<script lang="ts">
	import CareerCard from "@components/cards/career-card.svelte"
	import PageLoader from "@components/ui/page-loader.svelte"
	import PageState from "@components/ui/page-state.svelte"
	import { markdownSummary } from "@lib/utils/markdown"
	import Icon from "@components/ui/icon.svelte"
	import { useSchoolDetailPage } from "./use-school-detail.svelte"
	const page=useSchoolDetailPage()
</script>

<svelte:head><title>{page.school.name || "Escuela"} — Pathora</title></svelte:head>
<main>
	{#if page.resource.loading}<PageLoader />
	{:else if page.resource.error}<div class="page-shell">
		<PageState
			kind={page.resource.errorStatus === 404 ? "not-found" : "error"}
			title={page.resource.errorStatus === 404 ? "No encontramos esta escuela." : "No pudimos abrir la escuela."}
			message={page.resource.errorStatus === 404 ? "Es posible que la escuela ya no esté disponible o que el enlace sea incorrecto." : page.resource.error}
			onRetry={page.resource.reload}
		/>
	</div>
	{:else}
	<section class="hero" style={`--accent:${page.school.accent}`}>
		{#if page.school.image}<img src={page.school.image} alt={page.school.name} />{/if}
		<div class="veil"></div>
		<button class="back" onclick={() => history.back()}>← Escuelas</button>
		<div class="page-shell copy">
			<span>Escuela · {String(page.school.id).padStart(2, "0")}</span>
			<h1>{page.school.name}</h1>
			<p>{markdownSummary(page.school.description)}</p>
		</div>
	</section>
	<section class="page-shell facts">
		<div>
			<strong>{page.school.programs}</strong><span>programas académicos</span>
		</div>
		<div>
			<strong>{page.programs.reduce((sum, item) => sum + item.reviews, 0)}+</strong
			><span>experiencias compartidas</span>
		</div>
		<div><strong>3</strong><span>modalidades disponibles</span></div>
	</section>
	<section class="page-shell programs">
		<header>
			<div>
				<span>Oferta académica</span>
				<h2>Carreras para construir<br />tu propio camino.</h2>
			</div>
			<p>
				Conoce la duración, modalidad, valoración y enfoque de cada programa
				perteneciente a esta escuela.
			</p>
		</header>
		{#if page.programs.length}<div class="grid">
				{#each page.programs as career}<CareerCard {career} />{/each}
			</div>{:else}<div class="empty">
				<Icon name="search" size={26} />
				<h3>Oferta en actualización.</h3>
				<p>Estamos preparando los programas de esta escuela.</p>
			</div>{/if}
	</section>
	{/if}
</main>

<style>
	.hero {
		position: relative;
		min-height: 650px;
		display: flex;
		align-items: end;
		background: var(--accent);
		overflow: hidden;
		color: white;
	}
	.hero > img,
	.veil {
		position: absolute;
		inset: 0;
		width: 100%;
		height: 100%;
	}
	.hero > img {
		object-fit: cover;
	}
	.veil {
		background: linear-gradient(
				90deg,
				rgba(10, 10, 8, 0.82),
				rgba(10, 10, 8, 0.18)
			),
			linear-gradient(0deg, rgba(10, 10, 8, 0.62), transparent 55%);
	}
	.back {
		position: absolute;
		z-index: 2;
		top: 2rem;
		left: max(2rem, calc((100% - 1280px) / 2));
		border: 1px solid rgba(255, 255, 255, 0.28);
		border-radius: 999px;
		background: rgba(10, 10, 8, 0.25);
		backdrop-filter: blur(14px);
		color: white;
		padding: 0.65rem 1rem;
		font: inherit;
		cursor: pointer;
	}
	.copy {
		position: relative;
		z-index: 1;
		width: 100%;
		padding-bottom: 5rem;
	}
	.copy span,
	.programs header span {
		text-transform: uppercase;
		letter-spacing: 0.14em;
		font-size: 0.68rem;
		font-weight: 700;
	}
	.copy h1 {
		font-family: var(--display);
		font-size: clamp(4rem, 9vw, 8.5rem);
		max-width: 1000px;
		line-height: 0.86;
		letter-spacing: -0.07em;
		margin: 1rem 0 1.5rem;
	}
	.copy p {
		max-width: 680px;
		line-height: 1.7;
		color: rgba(255, 255, 255, 0.78);
	}
	.facts {
		display: grid;
		grid-template-columns: repeat(3, 1fr);
		border-bottom: 1px solid var(--line);
	}
	.facts div {
		padding: 2.5rem;
		border-right: 1px solid var(--line);
	}
	.facts div:last-child {
		border: 0;
	}
	.facts strong {
		font-family: var(--display);
		font-size: 2.8rem;
	}
	.facts span {
		display: block;
		margin-top: 0.3rem;
		color: var(--muted);
		font-size: 0.72rem;
	}
	.programs {
		padding-top: 8rem;
		padding-bottom: 10rem;
	}
	.programs header {
		display: flex;
		align-items: end;
		justify-content: space-between;
		gap: 3rem;
		margin-bottom: 3rem;
	}
	.programs header span {
		color: var(--muted);
	}
	h2 {
		font-family: var(--display);
		font-size: clamp(3rem, 6vw, 5.5rem);
		line-height: 0.94;
		letter-spacing: -0.06em;
		margin: 1rem 0 0;
	}
	.programs header p {
		max-width: 430px;
		line-height: 1.65;
		color: var(--muted);
	}
	.grid {
		display: grid;
		grid-template-columns: repeat(3, 1fr);
		gap: 1.2rem;
	}
	.empty {
		padding: 5rem;
		text-align: center;
		border: 1px dashed var(--line);
		border-radius: 22px;
	}
	.empty h3 {
		font-family: var(--display);
		font-size: 2rem;
		margin: 1rem 0 0.5rem;
	}
	.empty p {
		color: var(--muted);
	}
	@media (max-width: 900px) {
		.grid {
			grid-template-columns: repeat(2, 1fr);
		}
		.programs header {
			align-items: start;
			flex-direction: column;
		}
	}
	@media (max-width: 620px) {
		.hero {
			min-height: 570px;
		}
		.facts {
			grid-template-columns: 1fr;
		}
		.facts div {
			border-right: 0;
			border-bottom: 1px solid var(--line);
			padding: 1.5rem 0;
		}
		.grid {
			grid-template-columns: 1fr;
		}
		.programs {
			padding-top: 5rem;
		}
	}
	/* Keep the hero copy on the same grid as the rest of the page. */
	.hero .copy {
		width: min(1280px, calc(100% - 3rem));
		margin-inline: auto;
	}
	@media (max-width: 600px) {
		.hero .copy {
			width: min(100% - 2rem, 1280px);
		}
	}
</style>
