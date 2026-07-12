<script lang="ts">
	import LogoMark from "@components/brand/logo-mark.svelte"
</script>

<svelte:head><title>Acerca de — Pathora</title></svelte:head>
<main>
	<section class="hero page-shell">
		<div>
			<span>Acerca de Pathora</span>
			<h1>Elegir con menos ruido.<br /><em>Avanzar con más claridad.</em></h1>
		</div>
		<LogoMark size={150} />
	</section>
	<section class="manifesto page-shell">
		<aside>01 — Nuestra intención</aside>
		<div>
			<h2>Una decisión importante merece mejores herramientas.</h2>
			<p>
				Pathora nació para convertir catálogos dispersos, pensums difíciles de
				leer y opiniones aisladas en una experiencia clara, humana y útil.
			</p>
			<p>
				No elegimos por ti. Diseñamos el espacio para que puedas comparar,
				preguntar y reconocer qué camino se parece más a la vida que quieres
				construir.
			</p>
		</div>
	</section>
	<section class="values">
		<div class="page-shell">
			<header>
				<span>Cómo trabajamos</span>
				<h2>Principios antes que features.</h2>
			</header>
			<div class="value-grid">
				{#each [{ n: "01", t: "Claridad", p: "La información compleja debe sentirse navegable, no intimidante." }, { n: "02", t: "Honestidad", p: "Las experiencias reales tienen matices. No escondemos la conversación." }, { n: "03", t: "Agencia", p: "Tú marcas el ritmo, guardas opciones y tomas la decisión final." }, { n: "04", t: "Cuidado", p: "Privacidad, accesibilidad y diseño responsable desde el inicio." }] as value}<article
					>
						<span>{value.n}</span>
						<h3>{value.t}</h3>
						<p>{value.p}</p>
					</article>{/each}
			</div>
		</div>
	</section>
	<section class="team page-shell">
		<span>El equipo</span>
		<h2>Diseñado y construido por <b>codecraft</b>.</h2>
		<p>
			Un equipo que cree que la tecnología educativa puede sentirse tan
			considerada como la decisión que acompaña.
		</p>
	</section>
</main>

<style>
	.hero {
		min-height: 650px;
		padding-top: 7rem;
		padding-bottom: 7rem;
		display: flex;
		align-items: center;
		justify-content: space-between;
		gap: 4rem;
	}
	.hero span,
	.values header span,
	.team > span {
		text-transform: uppercase;
		letter-spacing: 0.14em;
		font-size: 0.68rem;
		font-weight: 700;
		color: var(--muted);
	}
	h1 {
		font-family: var(--display);
		font-size: clamp(4rem, 8vw, 8rem);
		line-height: 0.87;
		letter-spacing: -0.07em;
		margin: 1.5rem 0;
	}
	h1 em {
		font-style: normal;
		color: var(--muted);
	}
	.manifesto {
		display: grid;
		grid-template-columns: 1fr 2fr;
		gap: 5rem;
		padding-top: 8rem;
		padding-bottom: 9rem;
		border-top: 1px solid var(--line);
	}
	.manifesto aside {
		font-size: 0.68rem;
		color: var(--muted);
	}
	.manifesto h2,
	.values h2,
	.team h2 {
		font-family: var(--display);
		font-size: clamp(3rem, 6vw, 5.5rem);
		line-height: 0.95;
		letter-spacing: -0.055em;
		margin: 0 0 2rem;
	}
	.manifesto p {
		font-size: 1.1rem;
		line-height: 1.8;
		color: var(--muted);
		max-width: 760px;
	}
	.values {
		padding: 8rem 0;
		background: var(--ink);
		color: white;
	}
	.values header span {
		color: #888;
	}
	.value-grid {
		display: grid;
		grid-template-columns: repeat(4, 1fr);
		margin-top: 5rem;
		border-top: 1px solid #383934;
	}
	.value-grid article {
		padding: 2rem 1.5rem 1rem 0;
		border-right: 1px solid #383934;
	}
	.value-grid article + article {
		padding-left: 1.5rem;
	}
	.value-grid article:last-child {
		border: 0;
	}
	.value-grid article > span {
		color: var(--accent);
		font-size: 0.68rem;
	}
	.value-grid h3 {
		font-family: var(--display);
		font-size: 2rem;
		margin: 2rem 0 1rem;
	}
	.value-grid p {
		color: #999;
		font-size: 0.82rem;
		line-height: 1.7;
	}
	.team {
		padding-top: 9rem;
		padding-bottom: 10rem;
	}
	.team h2 {
		max-width: 950px;
		margin-top: 1rem;
	}
	.team h2 b {
		font-weight: 400;
		color: #7ca81a;
	}
	.team p {
		max-width: 620px;
		color: var(--muted);
		line-height: 1.7;
	}
	@media (max-width: 800px) {
		.hero {
			align-items: start;
			flex-direction: column;
		}
		.manifesto {
			grid-template-columns: 1fr;
			gap: 2rem;
		}
		.value-grid {
			grid-template-columns: 1fr 1fr;
		}
		.value-grid article:nth-child(2) {
			border-right: 0;
		}
	}
	@media (max-width: 520px) {
		.value-grid {
			grid-template-columns: 1fr;
		}
		.value-grid article,
		.value-grid article + article {
			padding: 1.5rem 0;
			border-right: 0;
			border-bottom: 1px solid #383934;
		}
	}
</style>
