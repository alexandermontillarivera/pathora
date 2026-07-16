<script lang="ts">
	import Icon from "@components/ui/icon.svelte"
	import {
		deleteLocalAiModel,
		disableLocalAi,
		enableLocalAi,
		inspectLocalAiCache,
	} from "@lib/ai/local-ai-service"
	import { LOCAL_AI_MODEL, localAi } from "@stores/local-ai"
	import { onMount } from "svelte"

	let changing = $state(false)
	let deleting = $state(false)

	onMount(() => {
		inspectLocalAiCache().catch(() => undefined)
	})

	async function toggle(event: Event) {
		const enabled = (event.currentTarget as HTMLInputElement).checked
		changing = true
		try {
			enabled ? await enableLocalAi() : await disableLocalAi()
		} catch {
			// The shared store exposes a user-facing error.
		} finally {
			changing = false
		}
	}

	async function removeModel() {
		deleting = true
		try {
			await deleteLocalAiModel()
		} finally {
			deleting = false
		}
	}
</script>

<div class="ai-heading">
	<div class="ai-mark"><Icon name="sparkles" size={18} /></div>
	<div>
		<strong>Asistente local</strong>
		<small>Orientación académica privada en este dispositivo.</small>
	</div>
</div>

<label class="toggle ai-toggle">
	<div>
		<strong>Activar P local</strong>
		<small>Descarga el modelo una vez y procesa tus preguntas sin enviarlas a un servidor de IA.</small>
	</div>
	<input
		type="checkbox"
		checked={$localAi.enabled}
		disabled={changing || deleting}
		onchange={toggle}
	/>
	<i></i>
</label>

{#if $localAi.status === "downloading"}
	<div class="download" aria-live="polite">
		<div class="download-meta">
			<span>Preparando el modelo</span><strong>{Math.round($localAi.progress * 100)}%</strong>
		</div>
		<progress max="1" value={$localAi.progress}></progress>
		<small>{$localAi.progressText}</small>
	</div>
{:else if $localAi.status === "ready"}
	<p class="status ready"><Icon name="check" size={15} /> Listo para conversar</p>
{:else if $localAi.error}
	<p class="status error" role="alert">{$localAi.error}</p>
{/if}

<div class="model-details">
	<div><span>Modelo</span><strong>Qwen 2.5 · 3B</strong></div>
	<div><span>Memoria estimada</span><strong>2.5 GB</strong></div>
	<div><span>Privacidad</span><strong>100% local</strong></div>
</div>
<p class="note">
	Requiere WebGPU, HTTPS y espacio libre. La primera descarga puede tardar varios minutos.
	El modelo técnico es <code>{LOCAL_AI_MODEL}</code>.
</p>
{#if $localAi.cached}
	<button class="delete-model" type="button" disabled={deleting || changing} onclick={removeModel}>
		<Icon name="trash" size={15} />{deleting ? "Eliminando…" : "Eliminar modelo del dispositivo"}
	</button>
{/if}

<style>
	.ai-heading {
		display: flex;
		align-items: center;
		gap: 0.75rem;
		margin: 0.8rem 0 1rem;
	}
	.ai-heading > div:last-child { display: flex; flex-direction: column; }
	.ai-heading small, .toggle small, .download small, .note { color: var(--muted); }
	.ai-mark {
		width: 36px;
		height: 36px;
		border-radius: 10px;
		background: var(--accent);
		display: grid;
		place-items: center;
		flex: 0 0 auto;
	}
	.ai-toggle { align-items: center; }
	.toggle {
		position: relative;
		display: flex;
		justify-content: space-between;
		gap: 1rem;
		padding: 1rem 0;
		border-top: 1px solid var(--line);
		cursor: pointer;
	}
	.toggle div { display: flex; flex-direction: column; }
	.toggle small { margin-top: 0.2rem; line-height: 1.45; }
	.toggle input { position: absolute; opacity: 0; }
	.toggle i {
		width: 38px;
		height: 22px;
		padding: 3px;
		border-radius: 99px;
		background: #d6d7d2;
		flex: 0 0 auto;
		transition: background-color 180ms ease-out;
	}
	.toggle i::after {
		content: "";
		display: block;
		width: 16px;
		height: 16px;
		border-radius: 50%;
		background: white;
		transition: transform 180ms cubic-bezier(0.22, 1, 0.36, 1);
	}
	.toggle input:checked + i { background: var(--ink); }
	.toggle input:checked + i::after { transform: translateX(16px); }
	.toggle input:focus-visible + i { outline: 3px solid rgba(80, 120, 0, 0.35); outline-offset: 2px; }
	.toggle input:disabled + i { opacity: 0.55; cursor: wait; }
	.download {
		padding: 0.9rem;
		border-radius: 10px;
		background: var(--surface-2);
	}
	.download-meta { display: flex; justify-content: space-between; font-size: 0.72rem; }
	progress {
		width: 100%;
		height: 6px;
		margin: 0.65rem 0 0.35rem;
		border: 0;
		accent-color: var(--ink);
	}
	.download small { display: block; font-size: 0.65rem; line-height: 1.4; }
	.status {
		display: flex;
		align-items: center;
		gap: 0.4rem;
		padding: 0.7rem 0.8rem;
		border-radius: 9px;
		font-size: 0.72rem;
	}
	.status.ready { background: #e8f4e5; color: #276331; }
	.status.error { background: #f7e9e7; color: #8d2c24; }
	.model-details { margin-top: 0.8rem; border-top: 1px solid var(--line); }
	.model-details div {
		display: flex;
		justify-content: space-between;
		gap: 1rem;
		padding: 0.65rem 0;
		border-bottom: 1px solid var(--line);
		font-size: 0.7rem;
	}
	.model-details span { color: var(--muted); }
	.note { margin: 0.9rem 0; font-size: 0.68rem; line-height: 1.55; }
	.note code { overflow-wrap: anywhere; }
	.delete-model {
		padding: 0;
		border: 0;
		background: none;
		color: #8d2c24;
		display: flex;
		align-items: center;
		gap: 0.4rem;
		font: inherit;
		font-size: 0.7rem;
		cursor: pointer;
	}
	.delete-model:disabled { opacity: 0.55; cursor: wait; }
	@media (prefers-reduced-motion: reduce) {
		.toggle i, .toggle i::after { transition: none; }
	}
</style>
