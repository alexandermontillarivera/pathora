<script lang="ts">
	let active = $state("overview")
	const sections = [
		{ id: "overview", label: "Resumen" },
		{ id: "data", label: "Datos que usamos" },
		{ id: "control", label: "Tu control" },
		{ id: "security", label: "Seguridad" },
	]
</script>

<svelte:head><title>Privacidad — Pathora</title></svelte:head>
<main class="page-shell">
	<header>
		<span>Privacidad en Pathora</span>
		<h1>Tus datos.<br />Tus decisiones.</h1>
		<p>
			Esta maqueta explica cómo diseñamos la experiencia de privacidad. La
			política definitiva se actualizará antes del lanzamiento.
		</p>
		<small>Última actualización · 11 de julio de 2026</small>
	</header>
	<div class="content">
		<nav>
			{#each sections as section}<button
					class:active={active === section.id}
					onclick={() => {
						active = section.id
						document
							.getElementById(section.id)
							?.scrollIntoView({ behavior: "smooth" })
					}}>{section.label}</button
				>{/each}
		</nav>
		<article>
			<section id="overview">
				<span>01</span>
				<div>
					<h2>Privacidad comprensible.</h2>
					<p>
						Queremos que entiendas qué información existe en Pathora y para qué
						se utiliza. Evitamos recopilar datos que no aporten valor directo a
						tu experiencia.
					</p>
				</div>
			</section>
			<section id="data">
				<span>02</span>
				<div>
					<h2>Datos que utilizamos.</h2>
					<p>
						Tu nombre, correo, país y descripción permiten identificar tu
						perfil. Las carreras guardadas, valoraciones y comentarios
						construyen tu experiencia personal y comunitaria.
					</p>
					<ul>
						<li>Información del perfil que decides compartir.</li>
						<li>Actividad dentro de carreras y escuelas.</li>
						<li>Información técnica básica para proteger la sesión.</li>
					</ul>
				</div>
			</section>
			<section id="control">
				<span>03</span>
				<div>
					<h2>Siempre bajo tu control.</h2>
					<p>
						Puedes editar tu perfil, comentarios y valoraciones. También puedes
						eliminar estos contenidos y solicitar la eliminación completa de tu
						cuenta.
					</p>
				</div>
			</section>
			<section id="security">
				<span>04</span>
				<div>
					<h2>Seguridad por diseño.</h2>
					<p>
						Las contraseñas se almacenan cifradas, las sesiones utilizan tokens
						de duración limitada y puedes recibir una alerta cuando detectemos
						un inicio de sesión desde un dispositivo nuevo.
					</p>
				</div>
			</section>
		</article>
	</div>
</main>

<style>
	main {
		padding-top: 7rem;
		padding-bottom: 10rem;
	}
	header {
		max-width: 850px;
	}
	header > span {
		text-transform: uppercase;
		letter-spacing: 0.14em;
		font-size: 0.68rem;
		font-weight: 700;
		color: var(--muted);
	}
	h1 {
		font-family: var(--display);
		font-size: clamp(4.5rem, 9vw, 8.5rem);
		line-height: 0.85;
		letter-spacing: -0.075em;
		margin: 1.5rem 0 2rem;
	}
	header p {
		max-width: 650px;
		font-size: 1.05rem;
		line-height: 1.7;
		color: var(--muted);
	}
	header small {
		display: block;
		margin-top: 2rem;
		color: #999;
	}
	.content {
		display: grid;
		grid-template-columns: 220px 1fr;
		gap: 6vw;
		margin-top: 8rem;
	}
	.content nav {
		position: sticky;
		top: 110px;
		align-self: start;
		display: flex;
		flex-direction: column;
		border-left: 1px solid var(--line);
	}
	nav button {
		padding: 0.7rem 1rem;
		border: 0;
		background: none;
		text-align: left;
		color: var(--muted);
		font: inherit;
		font-size: 0.75rem;
		cursor: pointer;
	}
	nav button.active {
		color: var(--ink);
		border-left: 2px solid var(--ink);
		margin-left: -1px;
	}
	article {
		max-width: 800px;
	}
	article section {
		display: grid;
		grid-template-columns: 50px 1fr;
		gap: 2rem;
		padding: 4rem 0;
		border-top: 1px solid var(--line);
		scroll-margin-top: 110px;
	}
	article section > span {
		font-size: 0.65rem;
		color: var(--muted);
	}
	h2 {
		font-family: var(--display);
		font-size: 3rem;
		letter-spacing: -0.05em;
		margin: 0 0 1.5rem;
	}
	article p,
	li {
		color: var(--muted);
		line-height: 1.8;
	}
	li + li {
		margin-top: 0.5rem;
	}
	@media (max-width: 700px) {
		.content {
			grid-template-columns: 1fr;
		}
		.content nav {
			position: static;
			flex-direction: row;
			overflow: auto;
			border-left: 0;
			border-bottom: 1px solid var(--line);
		}
		nav button {
			white-space: nowrap;
		}
		.content article section {
			grid-template-columns: 1fr;
			gap: 0.8rem;
		}
	}
</style>
