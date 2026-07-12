<script lang="ts">
 import InfiniteSentinel from "@components/ui/infinite-sentinel.svelte";import CareerCard from "@components/cards/career-card.svelte";import PageLoader from "@components/ui/page-loader.svelte";import Icon from "@components/ui/icon.svelte";import{useSearch}from"./use-search.svelte";const page=useSearch();let{schools,pageLoading,results,resultCount}=$derived(page);const{filters,resource,submit,clear,toggleSchool,toggleMode,setMinimumRating}=page
</script>

<svelte:head><title>Buscar carreras — Pathora</title></svelte:head>
<main class="page-shell page">
	{#if pageLoading}<PageLoader />{/if}
	<header>
		<span>Explorador académico</span>
		<h1>Encuentra una carrera.</h1>
		<form
			onsubmit={(event) => {
				event.preventDefault()
				submit()
			}}
		>
			<Icon name="search" size={21} /><input
				bind:value={filters.query}
				placeholder="Nombre, área o palabra clave"
				aria-label="Buscar carreras"
			/><button>Buscar</button>
		</form>
	</header>
	<div class="toolbar">
		<p>
			<strong>{resultCount}</strong>
			{resultCount === 1 ? "resultado" : "resultados"}{#if filters.appliedQuery}
				para “{filters.appliedQuery}”{/if}
		</p>
		<div>
			<button
				class="filter-toggle"
				onclick={() => (filters.mobileFilters = !filters.mobileFilters)}>Filtros</button
			><label
				>Ordenar por <select bind:value={filters.sort}
					><option value="relevance">Relevancia</option><option value="rating"
						>Mejor valoración</option
					><option value="name">Nombre A–Z</option></select
				></label
			>
		</div>
	</div>
	<div class="layout">
		<aside class:open={filters.mobileFilters}>
			<div class="aside-head">
				<strong>Filtros</strong><button onclick={clear}>Limpiar</button>
			</div>
			<fieldset>
				<legend>Escuela</legend>{#each schools as school}<label
						><input
							type="checkbox"
							checked={filters.selectedSchools.includes(school.name)}
							onchange={() => toggleSchool(school.name)}
						/><span>{school.name}</span></label
					>{/each}
			</fieldset>
			<fieldset>
				<legend>Modalidad</legend
				>{#each ["Presencial", "Virtual", "Mixta"] as mode}<label
						><input
							type="checkbox"
							checked={filters.modes.includes(mode)}
							onchange={() => toggleMode(mode)}
						/><span>{mode}</span></label
					>{/each}
			</fieldset>
			<fieldset>
				<legend>Valoración mínima</legend>{#each [4.5, 4, 0] as rating}<label
						><input
							type="radio"
							name="rating"
							checked={filters.minimumRating === rating}
							onchange={() => setMinimumRating(rating)}
						/><span
							>{rating ? `${rating}+ estrellas` : "Cualquier valoración"}</span
						></label
					>{/each}
			</fieldset>
			<button class="apply" onclick={() => (filters.mobileFilters = false)}
				>Ver {resultCount} resultados</button
			>
		</aside>
		<section>
			{#if results.length}<div class="grid">
					{#each results as career}<CareerCard {career} />{/each}
				</div>{:else if !pageLoading}<div class="empty">
					<i><Icon name="search" size={25} /></i><span>Sin coincidencias</span>
					<h2>Intenta ampliar tu búsqueda.</h2>
					<p>Prueba con menos filtros o utiliza términos más generales.</p>
					<button onclick={clear}>Limpiar filtros</button>
			</div>{/if}<InfiniteSentinel
					disabled={pageLoading || !resource.hasNext || resource.loading}
				loading={resource.loading && !pageLoading}
				onVisible={resource.loadMore}
			/>
		</section>
	</div>
</main>

<style>
	.page {
		padding-top: 5rem;
		padding-bottom: 10rem;
	}
	header > span {
		font-size: 0.68rem;
		text-transform: uppercase;
		letter-spacing: 0.14em;
		font-weight: 700;
		color: var(--muted);
	}
	h1 {
		font-family: var(--display);
		font-size: clamp(3.6rem, 7vw, 7rem);
		line-height: 0.9;
		letter-spacing: -0.07em;
		margin: 1rem 0 2rem;
	}
	form {
		max-width: 850px;
		height: 64px;
		padding: 0.5rem 0.5rem 0.5rem 1.1rem;
		border: 1px solid var(--line);
		border-radius: 18px;
		background: white;
		display: flex;
		align-items: center;
		gap: 0.8rem;
		box-shadow: 0 12px 40px rgba(30, 30, 24, 0.07);
	}
	form input {
		min-width: 0;
		flex: 1;
		border: 0;
		outline: 0;
		background: transparent;
		font: inherit;
		font-size: 1rem;
	}
	form button {
		height: 100%;
		padding: 0 1.5rem;
		border: 0;
		border-radius: 12px;
		background: var(--ink);
		color: white;
		font: inherit;
		font-weight: 650;
		cursor: pointer;
	}
	.toolbar {
		display: flex;
		align-items: center;
		justify-content: space-between;
		margin: 5rem 0 1.5rem;
		padding-bottom: 1rem;
		border-bottom: 1px solid var(--line);
	}
	.toolbar p {
		color: var(--muted);
		font-size: 0.8rem;
	}
	.toolbar p strong {
		color: var(--ink);
	}
	.toolbar > div {
		display: flex;
		align-items: center;
		gap: 0.7rem;
	}
	.toolbar label {
		font-size: 0.72rem;
		color: var(--muted);
	}
	select {
		margin-left: 0.5rem;
		border: 0;
		background: transparent;
		font: inherit;
		font-size: 0.72rem;
		color: var(--ink);
	}
	.filter-toggle {
		display: none;
	}
	.layout {
		display: grid;
		grid-template-columns: 240px 1fr;
		gap: 3rem;
	}
	aside {
		position: sticky;
		top: 100px;
		align-self: start;
	}
	.aside-head {
		display: flex;
		justify-content: space-between;
		margin-bottom: 2rem;
	}
	.aside-head button {
		border: 0;
		background: none;
		color: var(--muted);
		font: inherit;
		font-size: 0.68rem;
		text-decoration: underline;
		cursor: pointer;
	}
	fieldset {
		border: 0;
		border-bottom: 1px solid var(--line);
		padding: 0 0 1.5rem;
		margin: 0 0 1.5rem;
	}
	legend {
		font-size: 0.72rem;
		font-weight: 750;
		margin-bottom: 1rem;
	}
	fieldset label {
		display: flex;
		align-items: center;
		gap: 0.65rem;
		margin: 0.75rem 0;
		font-size: 0.75rem;
		color: #555;
		cursor: pointer;
	}
	fieldset input {
		width: 16px;
		height: 16px;
		accent-color: #181816;
	}
	.apply {
		display: none;
	}
	.grid {
		display: grid;
		grid-template-columns: repeat(2, minmax(0, 1fr));
		gap: 1.2rem;
	}
	.empty {
		min-height: 480px;
		padding: 5rem 2rem;
		border: 1px dashed var(--line);
		border-radius: 24px;
		display: grid;
		place-content: center;
		text-align: center;
	}
	.empty i {
		width: 52px;
		height: 52px;
		margin: auto;
		border-radius: 15px;
		background: var(--surface-2);
		display: grid;
		place-items: center;
		font-style: normal;
	}
	.empty > span {
		margin-top: 1.2rem;
		color: var(--muted);
		font-size: 0.7rem;
		text-transform: uppercase;
		letter-spacing: 0.1em;
	}
	.empty h2 {
		font-family: var(--display);
		font-size: 2.2rem;
		margin: 0.6rem 0;
	}
	.empty p {
		color: var(--muted);
	}
	.empty button {
		justify-self: center;
		border: 0;
		border-radius: 10px;
		padding: 0.7rem 1rem;
		background: var(--ink);
		color: white;
		font: inherit;
		cursor: pointer;
	}
	@media (max-width: 850px) {
		.layout {
			grid-template-columns: 1fr;
		}
		.filter-toggle {
			display: block;
			border: 1px solid var(--line);
			border-radius: 9px;
			background: white;
			padding: 0.55rem 0.8rem;
			font: inherit;
			font-size: 0.72rem;
		}
		aside {
			display: none;
			position: static;
			padding: 1.2rem;
			border: 1px solid var(--line);
			border-radius: 18px;
			background: var(--surface);
		}
		aside.open {
			display: block;
		}
		.apply {
			display: block;
			width: 100%;
			border: 0;
			border-radius: 10px;
			padding: 0.8rem;
			background: var(--ink);
			color: white;
			font: inherit;
		}
		.grid {
			grid-template-columns: repeat(2, minmax(0, 1fr));
		}
	}
	@media (max-width: 600px) {
		.page {
			padding-top: 3rem;
		}
		.toolbar {
			align-items: start;
			gap: 1rem;
			flex-direction: column;
			margin-top: 3rem;
		}
		.grid {
			grid-template-columns: 1fr;
		}
		form button {
			padding: 0 1rem;
		}
		.toolbar > div {
			width: 100%;
			justify-content: space-between;
		}
	}
</style>
