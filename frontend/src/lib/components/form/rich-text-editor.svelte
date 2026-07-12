<script lang="ts">
	import Icon from "@components/ui/icon.svelte"
	let {
		placeholder = "Escribe una respuesta…",
		onSubmit,
		compact = false,
	}: {
		placeholder?: string
		onSubmit: (html: string) => void
		compact?: boolean
	} = $props()
	let content = $state("")
	let editor: HTMLDivElement
	const tools = [
		{ command: "bold", icon: "bold", label: "Negrita" },
		{ command: "italic", icon: "italic", label: "Cursiva" },
		{ command: "insertUnorderedList", icon: "list", label: "Lista" },
		{ command: "formatBlock", icon: "quote", label: "Cita" },
	]
	function format(command: string) {
		editor.focus()
		document.execCommand(
			command,
			false,
			command === "formatBlock" ? "blockquote" : undefined,
		)
		content = editor.innerHTML
	}
	function addLink() {
		const url = window.prompt("Pega el enlace")
		if (url) {
			editor.focus()
			document.execCommand("createLink", false, url)
			content = editor.innerHTML
		}
	}
	function submit() {
		if (!editor.textContent?.trim()) return
		onSubmit(content)
		content = ""
		editor.innerHTML = ""
	}
</script>

<div class:compact class="editor">
	<div class="toolbar">
		{#each tools as tool}<button
				type="button"
				onclick={() => format(tool.command)}
				aria-label={tool.label}
				title={tool.label}><Icon name={tool.icon} size={17} /></button
			>{/each}<button
			type="button"
			onclick={addLink}
			aria-label="Enlace"
			title="Enlace"><Icon name="link" size={17} /></button
		>
	</div>
	<div
		class="canvas"
		class:empty={!content}
		bind:this={editor}
		bind:innerHTML={content}
		contenteditable="true"
		role="textbox"
		aria-multiline="true"
		data-placeholder={placeholder}
	></div>
	<div class="editor-foot">
		<span>Admite texto enriquecido</span><button
			type="button"
			class="submit"
			onclick={submit}>Publicar <Icon name="send" size={15} /></button
		>
	</div>
</div>

<style>
	.editor {
		border: 1px solid var(--line);
		border-radius: 16px;
		background: white;
		overflow: hidden;
	}
	.toolbar {
		display: flex;
		gap: 0.2rem;
		padding: 0.45rem;
		border-bottom: 1px solid var(--line);
		background: #fafaf7;
	}
	.toolbar button {
		width: 32px;
		height: 32px;
		border: 0;
		border-radius: 8px;
		background: none;
		color: var(--muted);
		display: grid;
		place-items: center;
		cursor: pointer;
	}
	.toolbar button:hover {
		background: var(--surface-2);
		color: var(--ink);
	}
	.canvas {
		min-height: 120px;
		padding: 1rem;
		outline: 0;
		font-size: 0.9rem;
		line-height: 1.65;
	}
	.canvas.empty:before {
		content: attr(data-placeholder);
		color: #aaa;
		pointer-events: none;
	}
	.canvas :global(blockquote) {
		border-left: 3px solid var(--accent);
		padding-left: 1rem;
		color: var(--muted);
	}
	.editor-foot {
		padding: 0.55rem 0.65rem 0.55rem 1rem;
		border-top: 1px solid var(--line);
		display: flex;
		align-items: center;
		justify-content: space-between;
	}
	.editor-foot > span {
		font-size: 0.62rem;
		color: var(--muted);
	}
	.submit {
		border: 0;
		border-radius: 9px;
		padding: 0.55rem 0.7rem;
		background: var(--ink);
		color: white;
		display: flex;
		align-items: center;
		gap: 0.35rem;
		font: inherit;
		font-size: 0.68rem;
		cursor: pointer;
	}
	.compact .canvas {
		min-height: 80px;
	}
	.compact .editor-foot > span {
		display: none;
	}
</style>
