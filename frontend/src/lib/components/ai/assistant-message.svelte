<script lang="ts">
	import { markdownToHtml } from "@lib/utils/markdown"
	let { content }: { content: string } = $props()
	let html = $derived(markdownToHtml(content))
</script>

<div class="message-content">{@html html}</div>

<style>
	.message-content :global(p) { margin: 0 0 0.55rem; }
	.message-content :global(p:last-child) { margin-bottom: 0; }
	.message-content :global(ul) { margin: 0.45rem 0; padding-left: 1.1rem; }
	.message-content :global(li) { margin: 0.25rem 0; }
	.message-content :global(h2),
	.message-content :global(h3) {
		margin: 0.75rem 0 0.35rem;
		font: inherit;
		font-weight: 750;
	}
	.message-content :global(blockquote) {
		margin: 0.5rem 0;
		padding: 0.6rem;
		border-radius: 8px;
		background: rgba(255, 255, 255, 0.55);
	}
</style>
