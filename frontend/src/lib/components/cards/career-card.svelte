<script lang="ts">
	import { link } from "@dvcol/svelte-simple-router/action"
	import Icon from "@components/ui/icon.svelte"
	import type { Career } from "@lib/types"
	import { currentUser } from '@stores/auth'
	import { savedService } from '@lib/services/saved-service'
	import { markdownSummary } from '@lib/utils/markdown'

	interface Props {
		career: Career
	}
	let { career }: Props = $props()
	let saved = $state(false)
	async function toggleSaved(event:MouseEvent){event.stopPropagation();saved=!saved;try{saved?await savedService.add(career.id):await savedService.remove(career.id)}catch{saved=!saved}}
</script>

<article class="card">
	<a
		class="card-link"
		href={`/careers/${career.id}`}
		use:link
		aria-label={`Ver ${career.name}`}
	></a>
	<div class="image-wrap">
		<img src={career.image} alt="" loading="lazy" /><span class="mode"
			>{career.mode}</span
		>{#if $currentUser}<button
			class:saved
			onclick={toggleSaved}
			aria-label="Guardar carrera"
			><Icon name={saved ? "bookmark" : "heart"} size={18} /></button>{/if}
	</div>
	<div class="body">
		<div class="eyebrow">
			<span>{career.school}</span><span>{career.rating?`★ ${career.rating}`:'Nuevo'}</span>
		</div>
		<h3>{career.name}</h3>
		<p>{markdownSummary(career.description)}</p>
		<div class="foot">
			<span>{career.terms} cuatrimestres</span><span class="open"
				>Ver carrera <Icon name="arrow" size={16} /></span
			>
		</div>
	</div>
</article>

<style>
	.card {
		min-width: 0;
		position: relative;
		background: var(--surface);
		border: 1px solid var(--line);
		border-radius: 24px;
		overflow: hidden;
		cursor: pointer;
		transition:
			transform 0.35s cubic-bezier(0.2, 0.8, 0.2, 1),
			box-shadow 0.35s;
	}
	.card:hover {
		transform: translateY(-5px);
		box-shadow: 0 22px 55px rgba(28, 28, 22, 0.12);
	}
	.card:active {
		transform: scale(0.985);
	}
	.card-link {
		position: absolute;
		z-index: 2;
		inset: 0;
		width: 100%;
		border: 0;
		background: transparent;
		cursor: pointer;
	}
	.card-link:focus-visible {
		outline: 3px solid #639000;
		outline-offset: -4px;
		border-radius: 24px;
	}
	.image-wrap {
		height: 220px;
		position: relative;
		overflow: hidden;
		background: #ddd;
	}
	.image-wrap img {
		width: 100%;
		height: 100%;
		object-fit: cover;
		transition: transform 0.7s cubic-bezier(0.2, 0.8, 0.2, 1);
	}
	.card:hover img {
		transform: scale(1.04);
	}
	.mode {
		position: absolute;
		left: 14px;
		top: 14px;
		padding: 0.42rem 0.68rem;
		border-radius: 999px;
		background: rgba(255, 255, 255, 0.82);
		backdrop-filter: blur(14px);
		font-size: 0.72rem;
		font-weight: 650;
	}
	.image-wrap button {
		position: absolute;
		z-index: 3;
		right: 14px;
		top: 14px;
		width: 36px;
		height: 36px;
		border: 0;
		border-radius: 50%;
		display: grid;
		place-items: center;
		background: rgba(255, 255, 255, 0.82);
		backdrop-filter: blur(14px);
		cursor: pointer;
	}
	.image-wrap button.saved {
		background: var(--accent);
	}
	.body {
		padding: 1.3rem;
	}
	.eyebrow,
	.foot {
		display: flex;
		justify-content: space-between;
		align-items: center;
		gap: 1rem;
	}
	.eyebrow {
		font-size: 0.7rem;
		text-transform: uppercase;
		letter-spacing: 0.08em;
		color: var(--muted);
	}
	h3 {
		font-family: var(--display);
		font-size: 1.65rem;
		letter-spacing: -0.035em;
		line-height: 1.05;
		margin: 0.75rem 0;
	}
	.body p {
		font-size: 0.9rem;
		color: var(--muted);
		line-height: 1.55;
		margin: 0 0 1.3rem;
		display: -webkit-box;
		line-clamp: 2;
		-webkit-line-clamp: 2;
		-webkit-box-orient: vertical;
		overflow: hidden;
	}
	.foot {
		padding-top: 1rem;
		border-top: 1px solid var(--line);
		font-size: 0.78rem;
		color: var(--muted);
	}
	.open {
		display: flex;
		align-items: center;
		gap: 0.4rem;
		color: var(--ink);
		font-weight: 650;
	}
</style>
