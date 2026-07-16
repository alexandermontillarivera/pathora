<script lang="ts">
 import PageLoader from "@components/ui/page-loader.svelte";import PageState from "@components/ui/page-state.svelte";import CareerCard from "@components/cards/career-card.svelte";import{link}from"@lib/actions/link";import Icon from "@components/ui/icon.svelte";import{useDiscover}from"./use-discover.svelte";const page=useDiscover();let{careers,schools,loading,loadError,totalCareers,featured,filtered}=$derived(page);const{form,modes,search}=page
</script>

<svelte:head><title>Pathora — Encuentra tu camino</title></svelte:head>

<main>
	{#if loading}<PageLoader />
	{:else if loadError}<div class="page-shell"><PageState title="No pudimos cargar Pathora." message={loadError} onRetry={page.reload} /></div>
	{:else}
	<section class="hero page-shell">
		<div class="hero-copy">
			<span class="kicker reveal">Decide con claridad</span>
			<h1 class="reveal delay-1">
				Tu futuro tiene<br /><em>más de una forma.</em>
			</h1>
			<p class="reveal delay-2">
				Explora carreras, compara pensums y escucha a quienes ya recorren el
				camino.
			</p>
			<form
				class="search reveal delay-3"
				onsubmit={(event) => {
					event.preventDefault()
					search()
				}}
			>
				<Icon name="search" /><input
					bind:value={form.query}
					placeholder="¿Qué te gustaría aprender?"
					aria-label="Buscar carreras"
				/><button type="submit">Buscar</button>
			</form>
		</div>
		<div class="hero-visual reveal delay-2">
			{#if featured}<img src={featured.image} alt={featured.name} />{:else}<div
					class="hero-placeholder"
				></div>{/if}
			{#if featured}<div class="floating-card">
					<div>
						<span>Programa destacado</span><strong>{featured.name}</strong>
					</div>
					<div class="score">{featured.rating || "Nuevo"}</div>
				</div>{/if}
			<div class="orbit">{totalCareers || "—"}<br /><span>carreras</span></div>
		</div>
	</section>

	<section class="page-shell explore">
		<div class="section-head">
			<div>
				<span class="kicker">Encuentra tu lugar</span>
				<h2>Carreras que inspiran.</h2>
			</div>
			<div class="filters">
				{#each modes as item}<button
						class:active={form.mode === item}
						onclick={() => (form.mode = item)}>{item}</button
					>{/each}
			</div>
		</div>
		<div class="career-grid">
			{#each filtered as career}<CareerCard {career} />{/each}
		</div>
		{#if filtered.length === 0}<div class="empty">
				<span>Sin carreras en esta modalidad</span>
				<h3>Prueba con otra opción.</h3>
				<button onclick={() => (form.mode = "Todas")}>Ver todas</button>
			</div>{/if}
	</section>

	<section class="schools page-shell">
		<div class="section-head">
			<div>
				<span class="kicker">Mundos por descubrir</span>
				<h2>Explora por escuela.</h2>
			</div>
			<a class="text-link" href="/schools" use:link
				>Ver todas <Icon name="arrow" size={17} /></a
			>
		</div>
		<div class="school-strip">
			{#each schools.slice(0, 4) as school}<a
					class="school-card"
					href={`/schools/${school.id}`}
					use:link
					style={`--card-accent:${school.accent}`}
				>
					<img src={school.image} alt="" />
					<div>
						<span>{school.programs} programas</span>
						<h3>{school.name}</h3>
					</div>
				</a>{/each}
		</div>
	</section>

	<section class="statement">
		<div class="page-shell statement-inner">
			<p>
				“Elegir no es encontrar una respuesta perfecta. Es descubrir qué
				preguntas quieres perseguir.”
			</p>
			<span>— El principio Pathora</span>
		</div>
	</section>
	{/if}
</main>

<style>
	.hero {
		min-height: 710px;
		display: grid;
		grid-template-columns: 1.05fr 0.95fr;
		align-items: center;
		gap: 6vw;
		padding-top: 4rem;
		padding-bottom: 7rem;
	}
	.kicker {
		font-size: 0.7rem;
		text-transform: uppercase;
		letter-spacing: 0.15em;
		font-weight: 700;
		color: var(--muted);
	}
	h1 {
		font-family: var(--display);
		font-size: clamp(3.6rem, 7vw, 7.2rem);
		line-height: 0.88;
		letter-spacing: -0.065em;
		margin: 1.5rem 0 2rem;
		font-weight: 540;
	}
	h1 em {
		font-weight: 400;
		color: var(--muted);
		font-style: normal;
	}
	.hero-copy > p {
		max-width: 570px;
		font-size: 1.15rem;
		line-height: 1.65;
		color: var(--muted);
	}
	.search {
		max-width: 620px;
		margin-top: 2.3rem;
		padding: 0.55rem 0.55rem 0.55rem 1rem;
		display: flex;
		align-items: center;
		gap: 0.8rem;
		border: 1px solid var(--line);
		border-radius: 17px;
		background: white;
		box-shadow: 0 12px 40px rgba(30, 30, 24, 0.08);
	}
	.search input {
		flex: 1;
		min-width: 0;
		border: 0;
		outline: 0;
		font: inherit;
		background: transparent;
	}
	.search button {
		border: 0;
		border-radius: 12px;
		padding: 0.85rem 1.2rem;
		background: var(--ink);
		color: white;
		font-weight: 650;
	}
	.hero-visual {
		height: 590px;
		position: relative;
	}
	.hero-visual > img {
		width: 100%;
		height: 100%;
		object-fit: cover;
		border-radius: 190px 190px 30px 30px;
		filter: saturate(0.82);
	}
	.hero-placeholder {
		width: 100%;
		height: 100%;
		border-radius: 190px 190px 30px 30px;
		background: var(--surface-2);
	}
	.floating-card {
		position: absolute;
		left: -3rem;
		bottom: 3rem;
		width: min(300px, 72%);
		padding: 1rem 1rem 1rem 1.2rem;
		display: flex;
		justify-content: space-between;
		align-items: center;
		background: rgba(255, 255, 255, 0.82);
		backdrop-filter: blur(20px);
		border: 1px solid rgba(255, 255, 255, 0.8);
		border-radius: 18px;
		box-shadow: var(--shadow);
	}
	.floating-card span {
		font-size: 0.65rem;
		text-transform: uppercase;
		letter-spacing: 0.08em;
		color: var(--muted);
	}
	.floating-card strong {
		display: block;
		margin-top: 0.35rem;
		font-size: 0.9rem;
	}
	.score {
		width: 48px;
		height: 48px;
		border-radius: 14px;
		display: grid;
		place-items: center;
		background: var(--accent);
		font-weight: 750;
	}
	.orbit {
		position: absolute;
		right: -2rem;
		top: 4rem;
		width: 94px;
		height: 94px;
		border-radius: 50%;
		display: grid;
		place-content: center;
		text-align: center;
		background: var(--ink);
		color: white;
		font-family: var(--display);
		font-size: 1.65rem;
		line-height: 0.75;
	}
	.orbit span {
		font-family: var(--body);
		font-size: 0.62rem;
		color: #aaa;
	}
	.explore,
	.schools {
		padding-top: 8rem;
		padding-bottom: 8rem;
	}
	.section-head {
		display: flex;
		justify-content: space-between;
		align-items: end;
		gap: 2rem;
		margin-bottom: 2.5rem;
	}
	.section-head h2 {
		font-family: var(--display);
		font-size: clamp(2.7rem, 5vw, 4.8rem);
		letter-spacing: -0.055em;
		line-height: 1;
		margin: 0.9rem 0 0;
	}
	.filters {
		display: flex;
		gap: 0.4rem;
		padding: 0.35rem;
		background: var(--surface-2);
		border-radius: 14px;
	}
	.filters button {
		border: 0;
		background: none;
		border-radius: 10px;
		padding: 0.7rem 0.9rem;
		font: inherit;
		font-size: 0.76rem;
		cursor: pointer;
		color: var(--muted);
	}
	.filters button.active {
		background: white;
		color: var(--ink);
		box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
	}
	.career-grid {
		display: grid;
		grid-template-columns: repeat(3, 1fr);
		gap: 1.2rem;
	}
	.empty {
		grid-column: 1/-1;
		text-align: center;
		padding: 6rem;
		background: var(--surface-2);
		border-radius: 24px;
	}
	.empty span {
		color: var(--muted);
		font-size: 0.8rem;
	}
	.empty h3 {
		font-family: var(--display);
		font-size: 2rem;
	}
	.empty button,
	.text-link {
		border: 0;
		background: none;
		font: inherit;
		cursor: pointer;
		font-weight: 650;
	}
	.text-link {
		display: flex;
		align-items: center;
		gap: 0.4rem;
		color: inherit;
		text-decoration: none;
	}
	.school-strip {
		display: grid;
		grid-template-columns: repeat(4, 1fr);
		gap: 0.85rem;
	}
	.school-card {
		height: 350px;
		position: relative;
		padding: 0;
		border: 0;
		border-radius: 22px;
		overflow: hidden;
		background: var(--card-accent);
		font: inherit;
		text-align: left;
		cursor: pointer;
		color: inherit;
		text-decoration: none;
	}
	.school-strip img {
		width: 100%;
		height: 100%;
		object-fit: cover;
		filter: grayscale(0.2);
		transition: transform 0.6s;
	}
	.school-card:hover img {
		transform: scale(1.04);
	}
	.school-card:after {
		content: "";
		position: absolute;
		inset: 40% 0 0;
		background: linear-gradient(transparent, rgba(0, 0, 0, 0.78));
	}
	.school-card div {
		position: absolute;
		z-index: 2;
		left: 1.2rem;
		right: 1.2rem;
		bottom: 1.2rem;
		color: white;
	}
	.school-strip span {
		font-size: 0.66rem;
		text-transform: uppercase;
		letter-spacing: 0.1em;
	}
	.school-strip h3 {
		font-family: var(--display);
		font-size: 1.5rem;
		line-height: 1.05;
		margin: 0.55rem 0;
	}
	.statement {
		background: var(--ink);
		color: white;
		padding: 9rem 0;
	}
	.statement-inner {
		max-width: 1000px;
	}
	.statement p {
		font-family: var(--display);
		font-size: clamp(2.6rem, 5vw, 5rem);
		line-height: 1.08;
		letter-spacing: -0.05em;
		margin: 0 0 2rem;
	}
	.statement span {
		color: #999;
		font-size: 0.78rem;
	}
	.reveal {
		animation: rise 0.7s both cubic-bezier(0.2, 0.8, 0.2, 1);
	}
	.delay-1 {
		animation-delay: 0.08s;
	}
	.delay-2 {
		animation-delay: 0.16s;
	}
	.delay-3 {
		animation-delay: 0.24s;
	}
	@keyframes rise {
		from {
			opacity: 0;
			transform: translateY(18px);
		}
		to {
			opacity: 1;
			transform: none;
		}
	}
	@media (max-width: 980px) {
		.hero {
			grid-template-columns: 1fr;
			padding-top: 5rem;
		}
		.hero-visual {
			height: 480px;
			margin-left: 3rem;
		}
		.career-grid {
			grid-template-columns: repeat(2, 1fr);
		}
		.school-strip {
			grid-template-columns: repeat(2, 1fr);
		}
	}
	@media (max-width: 640px) {
		.hero {
			padding-top: 3rem;
			min-height: 0;
		}
		.hero-visual {
			height: 390px;
			margin: 0 0 0 1rem;
		}
		.floating-card {
			left: -1rem;
		}
		.orbit {
			right: -0.5rem;
			width: 78px;
			height: 78px;
		}
		.search button {
			display: none;
		}
		.section-head {
			align-items: start;
			flex-direction: column;
		}
		.filters {
			max-width: 100%;
			overflow: auto;
		}
		.career-grid {
			grid-template-columns: 1fr;
		}
		.school-strip {
			grid-template-columns: 1fr 1fr;
		}
		.school-card {
			height: 250px;
		}
		.explore,
		.schools {
			padding-top: 5rem;
			padding-bottom: 5rem;
		}
	}
</style>
