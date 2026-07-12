<script lang="ts">
	import { link } from '@dvcol/svelte-simple-router/action'
	import LogoMark from "@components/brand/logo-mark.svelte"
</script>

<footer>
	<div class="page-shell footer-grid">
		<div>
			<div class="footer-brand"><LogoMark size={32} /><span>Pathora</span></div>
			<p>Una forma más clara y humana de elegir qué estudiar.</p>
		</div>
		<div>
			<strong>Explorar</strong><a href="/" use:link>Carreras</a>
			<a href="/schools" use:link>Escuelas</a>
			<a href="/community" use:link>Comunidad</a>
		</div>
		<div>
			<strong>Pathora</strong>
			<a href="/about" use:link>Acerca de</a>
			<a href="/privacy" use:link>Privacidad</a>
		</div>
	</div>
	<div class="page-shell footnote">
		<span>© 2026 Pathora</span><span>Desarrollado por <b>codecraft</b></span>
	</div>
</footer>

<style>
	footer {
		padding: 6rem 0 2rem;
		background: #161714;
		color: white;
	}
	.footer-grid {
		display: grid;
		grid-template-columns: 2fr 1fr 1fr;
		gap: 4rem;
	}
	.footer-brand {
		display: flex;
		align-items: center;
		gap: 0.7rem;
		font-weight: 700;
		font-size: 1.15rem;
	}
	footer p {
		max-width: 280px;
		color: #999;
		line-height: 1.6;
		font-size: 0.85rem;
	}
	.footer-grid > div {
		display: flex;
		flex-direction: column;
		align-items: start;
		gap: 0.75rem;
	}
	.footer-grid strong {
		font-size: 0.75rem;
		margin-bottom: 0.5rem;
	}
	.footer-grid a {
		border: 0;
		background: none;
		color: #999;
		padding: 0;
		font: inherit;
		font-size: 0.78rem;
		cursor: pointer;
		text-decoration: none;
	}
	.footer-grid a:hover {
		color: white;
	}
	.footnote {
		display: flex;
		justify-content: space-between;
		margin-top: 5rem;
		padding-top: 1.5rem;
		border-top: 1px solid #333;
		color: #777;
		font-size: 0.68rem;
	}
	.footnote b {
		color: var(--accent);
		font-weight: 600;
	}
	@media (max-width: 700px) {
		.footer-grid {
			grid-template-columns: 1fr 1fr;
		}
		.footer-grid > div:first-child {
			grid-column: 1/-1;
		}
	}
	@media (max-width: 500px) {
		.footer-grid {
			grid-template-columns: 1fr;
		}
		.footer-grid > div:first-child {
			grid-column: auto;
		}
		.footnote {
			gap: 1rem;
			flex-direction: column;
		}
	}
</style>
