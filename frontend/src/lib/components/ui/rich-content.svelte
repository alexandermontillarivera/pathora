<script lang="ts">
	import { markdownToHtml } from "@lib/utils/markdown"
	let { content }: { content: string } = $props()
	let html = $derived(markdownToHtml(content))
</script>

<div class="content">{@html html}</div>

<style>
	.content :global(h2) {
		font-family: var(--display);
		font-size: 2rem;
		letter-spacing: -0.035em;
		margin: 2rem 0 0.7rem;
	}
	.content :global(h3) {
		font-size: 1rem;
		margin: 1.5rem 0 0.5rem;
	}
	.content :global(p),
	.content :global(li) {
		color: var(--muted);
		line-height: 1.75;
	}
	.content :global(ul) {
		padding-left: 1.2rem;
	}
	.content :global(blockquote) {
		margin: 1.5rem 0;
		padding: 1rem 1.2rem;
		border-left: 3px solid var(--accent);
		background: var(--surface-2);
		color: var(--muted);
	}
</style>
