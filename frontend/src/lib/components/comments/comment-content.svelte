<script lang="ts">
	import { richTextHtml } from "@lib/utils/rich-text"

	let { content, compact = false }: { content: unknown; compact?: boolean } = $props()
	let html = $derived(richTextHtml(content))
</script>

<div class="rich-comment" class:compact>{@html html}</div>

<style>
	.rich-comment {
		max-width: 72ch;
		color: #444540;
		font-size: 1rem;
		line-height: 1.7;
		text-wrap: pretty;
	}

	.rich-comment :global(p) {
		margin: 0.45rem 0;
	}

	.rich-comment :global(strong),
	.rich-comment :global(b) {
		color: var(--ink);
		font-weight: 700;
	}

	.rich-comment :global(em),
	.rich-comment :global(i) {
		font-style: italic;
	}

	.rich-comment :global(ul),
	.rich-comment :global(ol) {
		margin: 0.65rem 0;
		padding-left: 1.35rem;
	}

	.rich-comment :global(li + li) {
		margin-top: 0.25rem;
	}

	.rich-comment :global(blockquote) {
		margin: 0.75rem 0;
		padding: 0.7rem 0.9rem;
		border: 1px solid var(--line);
		border-radius: 10px;
		background: var(--surface-2);
		color: #555650;
	}

	.rich-comment :global(a) {
		color: #456c00;
		font-weight: 600;
		text-decoration-thickness: 1px;
		text-underline-offset: 0.16em;
	}

	.rich-comment :global(a:hover) {
		color: var(--ink);
	}

	.rich-comment.compact {
		font-size: 0.86rem;
		line-height: 1.6;
	}
</style>
