<script lang="ts">
	import { link } from "@lib/actions/link"
	import Icon from "./icon.svelte"

	let {
		kind = "error",
		title,
		message,
		compact = false,
		onRetry,
	}: {
		kind?: "error" | "not-found" | "empty"
		title: string
		message?: string
		compact?: boolean
		onRetry?: () => void | Promise<void>
	} = $props()
	let retrying = $state(false)
	async function retry() {
		if (!onRetry || retrying) return
		retrying = true
		try { await onRetry() } finally { retrying = false }
	}
</script>

<section class:compact class="state" role={kind === "error" ? "alert" : undefined}>
	<div class="mark" aria-hidden="true">
		<span>{kind === "not-found" ? "404" : kind === "empty" ? "0" : "!"}</span>
		<Icon name={kind === "not-found" ? "mapQuestion" : kind === "empty" ? "compass" : "cloudOff"} size={28} />
	</div>
	<div class="copy">
		<span class="eyebrow">{kind === "not-found" ? "Ruta no encontrada" : kind === "empty" ? "Sin resultados" : "Algo interrumpió el camino"}</span>
		{#if compact}<h2>{title}</h2>{:else}<h1>{title}</h1>{/if}
		{#if message}<p>{message}</p>{/if}
		<div class="actions">
			{#if onRetry}<button onclick={retry} disabled={retrying} aria-busy={retrying}>
				<Icon name="refresh" size={17} />{retrying ? "Reintentando…" : "Intentar de nuevo"}
			</button>{/if}
			<a href="/" use:link><Icon name="home" size={17} />Volver al inicio</a>
		</div>
	</div>
</section>

<style>
	.state{min-height:58vh;display:grid;grid-template-columns:minmax(180px,.55fr) 1fr;align-items:center;gap:clamp(2rem,7vw,7rem);padding:clamp(4rem,8vw,8rem) 0;border-block:1px solid var(--line)}
	.mark{position:relative;min-height:220px;display:grid;place-items:center;background:var(--surface-2);overflow:hidden;color:var(--muted)}
	.mark span{position:absolute;font-family:var(--display);font-size:clamp(5rem,12vw,9rem);line-height:1;color:rgba(20,20,18,.06)}
	.copy{max-width:620px}.eyebrow{text-transform:uppercase;letter-spacing:.14em;font-size:.67rem;font-weight:700;color:var(--muted)}
	h1,h2{font-family:var(--display);font-weight:520;letter-spacing:-.05em;line-height:1;margin:1rem 0 1.2rem}h1{font-size:clamp(2.8rem,6vw,5.5rem)}h2{font-size:clamp(2rem,4vw,3.2rem)}
	p{max-width:550px;color:var(--muted);line-height:1.7}.actions{display:flex;gap:.65rem;flex-wrap:wrap;margin-top:1.8rem}.actions button,.actions a{min-height:44px;padding:.75rem 1rem;border:1px solid var(--line);border-radius:11px;display:inline-flex;align-items:center;justify-content:center;gap:.45rem;background:white;color:var(--ink);font:inherit;font-size:.82rem;font-weight:650;text-decoration:none;cursor:pointer}.actions button{background:var(--ink);color:white;border-color:var(--ink)}.actions button:disabled{opacity:.65;cursor:wait}
	.compact{min-height:300px;grid-template-columns:130px 1fr;gap:2rem;padding:3rem}.compact .mark{min-height:130px}.compact .mark span{font-size:4.8rem}
	@media(max-width:680px){.state{grid-template-columns:1fr;gap:1.5rem}.mark{min-height:150px}.compact{padding:2rem 0}.compact .mark{display:none}.actions>*{flex:1}}
</style>
