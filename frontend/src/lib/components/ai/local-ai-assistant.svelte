<script lang="ts">
	import AssistantMessage from "@components/ai/assistant-message.svelte"
	import Icon from "@components/ui/icon.svelte"
	import { link } from "@dvcol/svelte-simple-router/action"
	import { careersMentionedIn } from "@lib/ai/catalog-context"
	import {
		ensureLocalAiEngine,
		stopLocalAiGeneration,
		streamLocalAiAnswer,
	} from "@lib/ai/local-ai-service"
	import type { ApiCareer } from "@lib/api/api-types"
	import { localAi } from "@stores/local-ai"
	import type { ChatCompletionMessageParam } from "@mlc-ai/web-llm"
	import { tick } from "svelte"

	type Message = {
		id: string
		role: "user" | "assistant"
		content: string
		careers?: ApiCareer[]
		error?: boolean
	}

	const welcome: Message = {
		id: "welcome",
		role: "assistant",
		content:
			"Hola, soy **P**, tu orientador local. Puedo comparar carreras, explorar intereses y explicarte pensums usando el catálogo de Pathora.",
	}
	const suggestions = [
		"Ayúdame a descubrir qué estudiar",
		"Compara carreras tecnológicas",
		"Quiero una carrera con impacto ambiental",
	]
	let panelOpen = $state(false)
	let input = $state("")
	let sending = $state(false)
	let messages = $state<Message[]>([welcome])
	let inputElement = $state<HTMLTextAreaElement>()
	let messagesElement = $state<HTMLDivElement>()

	async function openPanel() {
		panelOpen = true
		await tick()
		inputElement?.focus()
		if ($localAi.status !== "ready" && $localAi.status !== "downloading")
			ensureLocalAiEngine().catch(() => undefined)
	}

	async function scrollToLatest() {
		await tick()
		messagesElement?.scrollTo({ top: messagesElement.scrollHeight, behavior: "smooth" })
	}

	async function ask(question = input) {
		const value = question.trim()
		if (!value || sending || $localAi.status !== "ready") return
		input = ""
		const userMessage: Message = {
			id: crypto.randomUUID(),
			role: "user",
			content: value,
		}
		const assistantId = crypto.randomUUID()
		const history: ChatCompletionMessageParam[] = messages
			.filter((message) => message.id !== "welcome" && !message.error)
			.map((message) => ({ role: message.role, content: message.content }))
		messages = [
			...messages,
			userMessage,
			{ id: assistantId, role: "assistant", content: "" },
		]
		sending = true
		await scrollToLatest()
		try {
			const answer = await streamLocalAiAnswer(value, history, (content) => {
				messages = messages.map((message) =>
					message.id === assistantId ? { ...message, content } : message,
				)
				void scrollToLatest()
			})
			const careers = await careersMentionedIn(answer)
			messages = messages.map((message) =>
				message.id === assistantId ? { ...message, careers } : message,
			)
		} catch {
			messages = messages.map((message) =>
				message.id === assistantId
					? {
							...message,
							content:
								"No pude completar la respuesta. Puedes intentarlo nuevamente o revisar el estado del modelo en Ajustes.",
							error: true,
						}
					: message,
			)
		} finally {
			sending = false
			await scrollToLatest()
			inputElement?.focus()
		}
	}

	function stop() {
		stopLocalAiGeneration()
		sending = false
	}

	function resetConversation() {
		if (sending) stop()
		messages = [welcome]
	}
</script>

{#if $localAi.enabled}
	{#if panelOpen}
		<div class="assistant-panel" role="dialog" aria-label="Asistente local de Pathora">
			<header>
				<div class="identity">
					<span><Icon name="sparkles" size={18} /></span>
					<div><strong>P local</strong><small><i></i> Privado · en tu dispositivo</small></div>
				</div>
				<div class="header-actions">
					<button type="button" onclick={resetConversation} aria-label="Nueva conversación">
						<Icon name="edit" size={17} />
					</button>
					<button type="button" onclick={() => (panelOpen = false)} aria-label="Cerrar asistente">
						<Icon name="close" size={18} />
					</button>
				</div>
			</header>

			{#if $localAi.status === "downloading"}
				<div class="model-state" aria-live="polite">
					<div class="model-icon"><Icon name="download" size={22} /></div>
					<strong>Preparando tu orientador</strong>
					<p>La primera vez descargamos el modelo. Después quedará guardado en este dispositivo.</p>
					<progress max="1" value={$localAi.progress}></progress>
					<small>{Math.round($localAi.progress * 100)}% · {$localAi.progressText}</small>
				</div>
			{:else if $localAi.status === "unsupported" || $localAi.status === "error"}
				<div class="model-state error" role="alert">
					<div class="model-icon"><Icon name="brain" size={22} /></div>
					<strong>El asistente no pudo iniciar</strong>
					<p>{$localAi.error}</p>
					<a href="/profile" use:link>Abrir ajustes</a>
				</div>
			{:else}
				<div class="messages" bind:this={messagesElement} aria-live="polite">
					{#each messages as message}
						<article class:user={message.role === "user"} class:error={message.error}>
							{#if message.role === "assistant"}<span class="avatar">P</span>{/if}
							<div class="bubble">
								{#if message.content}
									<AssistantMessage content={message.content} />
								{:else}<span class="thinking"><i></i><i></i><i></i></span>{/if}
								{#if message.careers?.length}
									<div class="career-links">
										{#each message.careers as career}
											<a href={`/careers/${career.id}`} use:link>{career.name}<Icon name="arrow" size={13} /></a>
										{/each}
									</div>
								{/if}
							</div>
						</article>
					{/each}
					{#if messages.length === 1}
						<div class="suggestions">
							{#each suggestions as suggestion}<button type="button" onclick={() => ask(suggestion)}>{suggestion}</button>{/each}
						</div>
					{/if}
				</div>
				<form
					onsubmit={(event) => {
						event.preventDefault()
						void ask()
					}}
				>
					<textarea
						bind:this={inputElement}
						bind:value={input}
						rows="1"
						maxlength="500"
						placeholder="Pregúntame sobre carreras…"
						onkeydown={(event) => {
							if (event.key === "Enter" && !event.shiftKey) {
								event.preventDefault()
								void ask()
							}
						}}
					></textarea>
					{#if sending}
						<button class="send stop" type="button" onclick={stop} aria-label="Detener respuesta"><Icon name="stop" size={17} /></button>
					{:else}
						<button class="send" type="submit" disabled={!input.trim()} aria-label="Enviar pregunta"><Icon name="send" size={17} /></button>
					{/if}
				</form>
				<p class="disclaimer">P puede equivocarse. Verifica los detalles importantes en cada carrera.</p>
			{/if}
		</div>
	{/if}
	<button
		class="assistant-trigger"
		class:open={panelOpen}
		type="button"
		onclick={() => (panelOpen ? (panelOpen = false) : void openPanel())}
		aria-label={panelOpen ? "Cerrar asistente local" : "Abrir asistente local"}
		aria-expanded={panelOpen}
	>
		{#if panelOpen}<Icon name="close" size={20} />{:else}<span>P</span>{/if}
		{#if $localAi.status === "downloading"}<i class="progress-ring" style={`--progress:${$localAi.progress * 360}deg`}></i>{/if}
	</button>
{/if}

<style>
	.assistant-trigger {
		position: fixed;
		z-index: 70;
		right: 1.5rem;
		bottom: 1.5rem;
		width: 54px;
		height: 54px;
		border: 0;
		border-radius: 15px;
		background: var(--ink);
		color: white;
		display: grid;
		place-items: center;
		font: inherit;
		font-size: 1.15rem;
		font-weight: 800;
		cursor: pointer;
		box-shadow: 0 6px 8px rgba(18, 19, 16, 0.2);
		transition: transform 180ms cubic-bezier(0.22, 1, 0.36, 1);
	}
	.assistant-trigger:hover { transform: translateY(-2px); }
	.assistant-trigger > span { color: var(--accent); }
	.assistant-trigger.open { background: #33342f; }
	.progress-ring {
		position: absolute;
		inset: -3px;
		border-radius: 18px;
		background: conic-gradient(var(--accent) var(--progress), transparent 0);
		mask: radial-gradient(farthest-side, transparent calc(100% - 2px), #000 0);
	}
	.assistant-panel {
		position: fixed;
		z-index: 65;
		right: 1.5rem;
		bottom: 5.6rem;
		width: min(420px, calc(100vw - 2rem));
		height: min(650px, calc(100dvh - 7.5rem));
		border: 1px solid var(--line);
		border-radius: 16px;
		background: #fff;
		box-shadow: 0 8px 8px rgba(18, 19, 16, 0.12);
		display: flex;
		flex-direction: column;
		overflow: hidden;
		animation: panel-in 200ms cubic-bezier(0.22, 1, 0.36, 1);
	}
	@keyframes panel-in { from { opacity: 0; transform: translateY(10px) scale(0.98); } }
	header {
		min-height: 68px;
		padding: 0.8rem 0.9rem;
		border-bottom: 1px solid var(--line);
		display: flex;
		align-items: center;
		justify-content: space-between;
	}
	.identity, .identity > div, .header-actions { display: flex; align-items: center; }
	.identity { gap: 0.65rem; }
	.identity > span {
		width: 38px; height: 38px; border-radius: 10px; background: var(--accent);
		display: grid; place-items: center;
	}
	.identity > div { align-items: flex-start; flex-direction: column; }
	.identity strong { font-size: 0.82rem; }
	.identity small { color: var(--muted); font-size: 0.64rem; }
	.identity small i { display: inline-block; width: 6px; height: 6px; border-radius: 50%; background: #4a9b4e; }
	.header-actions { gap: 0.2rem; }
	.header-actions button {
		width: 34px; height: 34px; border: 0; border-radius: 8px; background: transparent;
		display: grid; place-items: center; color: var(--muted); cursor: pointer;
	}
	.header-actions button:hover { background: var(--surface-2); color: var(--ink); }
	.messages { flex: 1; overflow-y: auto; padding: 1rem; scroll-behavior: smooth; }
	article { display: flex; align-items: flex-start; gap: 0.55rem; margin-bottom: 1rem; }
	article.user { justify-content: flex-end; }
	.avatar {
		width: 26px; height: 26px; border-radius: 8px; background: var(--ink); color: var(--accent);
		display: grid; place-items: center; flex: 0 0 auto; font-size: 0.66rem; font-weight: 800;
	}
	.bubble {
		max-width: 84%; padding: 0.72rem 0.8rem; border-radius: 4px 12px 12px 12px;
		background: var(--surface-2); color: #383934; font-size: 0.78rem; line-height: 1.55;
	}
	.user .bubble { border-radius: 12px 4px 12px 12px; background: var(--ink); color: white; }
	article.error .bubble { background: #f7e9e7; color: #7d2922; }
	.thinking { display: flex; gap: 0.25rem; padding: 0.25rem; }
	.thinking i { width: 5px; height: 5px; border-radius: 50%; background: #777; animation: think 900ms infinite alternate; }
	.thinking i:nth-child(2) { animation-delay: 150ms; }
	.thinking i:nth-child(3) { animation-delay: 300ms; }
	@keyframes think { to { opacity: 0.3; transform: translateY(-2px); } }
	.career-links { display: flex; flex-wrap: wrap; gap: 0.35rem; margin-top: 0.7rem; padding-top: 0.6rem; border-top: 1px solid #d7d8d2; }
	.career-links a {
		padding: 0.35rem 0.45rem; border-radius: 7px; background: white; color: var(--ink);
		display: flex; align-items: center; gap: 0.25rem; font-size: 0.65rem; font-weight: 650; text-decoration: none;
	}
	.suggestions { display: flex; flex-direction: column; align-items: flex-start; gap: 0.4rem; padding-left: 2.1rem; }
	.suggestions button {
		padding: 0.55rem 0.65rem; border: 1px solid var(--line); border-radius: 9px; background: white;
		color: #55564f; font: inherit; font-size: 0.7rem; text-align: left; cursor: pointer;
	}
	form {
		margin: 0 0.8rem;
		padding: 0.45rem 0.45rem 0.45rem 0.75rem;
		border: 1px solid var(--line);
		border-radius: 12px;
		display: flex;
		align-items: end;
		gap: 0.5rem;
	}
	textarea { flex: 1; min-height: 36px; max-height: 100px; padding: 0.45rem 0; border: 0; outline: 0; resize: none; font: inherit; font-size: 0.78rem; line-height: 1.45; }
	.send { width: 36px; height: 36px; border: 0; border-radius: 9px; background: var(--ink); color: white; display: grid; place-items: center; cursor: pointer; }
	.send:disabled { opacity: 0.35; cursor: default; }
	.send.stop { background: #8d2c24; }
	.disclaimer { margin: 0.45rem 1rem 0.65rem; text-align: center; color: var(--muted); font-size: 0.58rem; }
	.model-state { margin: auto; max-width: 310px; padding: 2rem; text-align: center; }
	.model-icon { width: 48px; height: 48px; margin: 0 auto 1rem; border-radius: 13px; background: var(--accent); display: grid; place-items: center; }
	.model-state strong { display: block; }
	.model-state p { color: var(--muted); font-size: 0.76rem; line-height: 1.55; }
	.model-state progress { width: 100%; height: 7px; accent-color: var(--ink); }
	.model-state small { display: block; margin-top: 0.5rem; color: var(--muted); font-size: 0.62rem; }
	.model-state a { color: var(--ink); font-size: 0.75rem; font-weight: 700; }
	@media (max-width: 560px) {
		.assistant-panel { inset: 0; width: 100%; height: 100dvh; border: 0; border-radius: 0; }
		.assistant-trigger { right: 1rem; bottom: 1rem; }
		.assistant-trigger.open { display: none; }
	}
	@media (prefers-reduced-motion: reduce) {
		.assistant-panel, .thinking i { animation: none; }
		.assistant-trigger { transition: none; }
		.messages { scroll-behavior: auto; }
	}
</style>
