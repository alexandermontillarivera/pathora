<script lang="ts">
	import Skeleton from "./skeleton.svelte"
</script>

<article>
	<Skeleton height="220px" radius="0" />
	<div>
		<Skeleton height=".7rem" /><Skeleton height="2rem" /><Skeleton
			height="3rem"
		/><Skeleton height="1px" />
	</div>
</article>

<style>
	article {
		border: 1px solid var(--line);
		border-radius: 24px;
		overflow: hidden;
		background: var(--surface);
	}
	div {
		padding: 1.3rem;
		display: grid;
		gap: 1rem;
	}
</style>
