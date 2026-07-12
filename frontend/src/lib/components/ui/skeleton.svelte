<script lang="ts">
	let {
		height = "1rem",
		radius = "10px",
	}: { height?: string; radius?: string } = $props()
</script>

<span
	class="skeleton"
	style={`height:${height};border-radius:${radius}`}
	aria-hidden="true"
></span>

<style>
	.skeleton {
		display: block;
		width: 100%;
		background: linear-gradient(100deg, #ebeae5 25%, #f8f7f3 42%, #ebeae5 60%);
		background-size: 240% 100%;
		animation: shimmer 1.35s infinite;
	}
	@keyframes shimmer {
		to {
			background-position-x: -240%;
		}
	}
	@media (prefers-reduced-motion: reduce) {
		.skeleton {
			animation: none;
		}
	}
</style>
