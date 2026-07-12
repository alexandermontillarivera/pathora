<script lang="ts">
	import { link } from "@dvcol/svelte-simple-router/action"
	import Icon from "@components/ui/icon.svelte"
</script>

<svelte:head><title>Página no encontrada — Pathora</title></svelte:head>
<main class="page-shell">
	<div class="code">404</div>
	<div class="copy">
		<span>Te saliste del mapa</span>
		<h1>Este camino<br />no lleva a ningún lado.</h1>
		<p>
			La página que buscas cambió de ruta o nunca estuvo aquí. Volvamos a un
			lugar conocido.
		</p>
		<a href="/" use:link>Volver a descubrir <Icon name="arrow" size={17} /></a>
	</div>
</main>

<style>
	main {
		min-height: 75vh;
		display: grid;
		grid-template-columns: 0.8fr 1.2fr;
		gap: 5vw;
		align-items: center;
		padding-top: 4rem;
		padding-bottom: 7rem;
	}
	.code {
		font-family: var(--display);
		font-size: clamp(9rem, 24vw, 24rem);
		letter-spacing: -0.09em;
		line-height: 0.7;
		color: var(--surface-2);
		user-select: none;
	}
	.copy > span {
		text-transform: uppercase;
		letter-spacing: 0.14em;
		font-size: 0.68rem;
		font-weight: 700;
		color: var(--muted);
	}
	h1 {
		font-family: var(--display);
		font-size: clamp(3.5rem, 7vw, 7rem);
		line-height: 0.88;
		letter-spacing: -0.065em;
		margin: 1.4rem 0 2rem;
	}
	.copy p {
		max-width: 520px;
		color: var(--muted);
		line-height: 1.7;
	}
	.copy a {
		margin-top: 1.2rem;
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
		text-decoration: none;
	}
	@media (max-width: 760px) {
		main {
			grid-template-columns: 1fr;
		}
		.code {
			font-size: 10rem;
			margin-bottom: -2rem;
		}
	}
</style>
