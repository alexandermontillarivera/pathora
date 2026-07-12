<script lang="ts">
	import { cubicOut } from "svelte/easing"
	import { fly } from "svelte/transition"

	let {
		onVisible,
		disabled = false,
		loading = false,
		label = "Cargando más resultados",
	}: {
		onVisible: () => void | Promise<void>
		disabled?: boolean
		loading?: boolean
		label?: string
	} = $props()
	let node: HTMLDivElement
	let visible = $state(false)
	let shownAt = 0
	const minimumVisibleMs = 600
	const reduceMotion = window.matchMedia(
		"(prefers-reduced-motion: reduce)",
	).matches

	$effect(() => {
		if (disabled) return
		const observer = new IntersectionObserver(
			(entries) => {
				if (entries[0]?.isIntersecting) onVisible()
			},
			{ rootMargin: "300px" },
		)
		observer.observe(node)
		return () => observer.disconnect()
	})
	$effect(() => {
		let timeout: ReturnType<typeof setTimeout> | undefined
		if (loading) {
			visible = true
			shownAt = performance.now()
		} else if (visible) {
			const remaining = Math.max(
				0,
				minimumVisibleMs - (performance.now() - shownAt),
			)
			timeout = setTimeout(() => (visible = false), remaining)
		}
		return () => {
			if (timeout) clearTimeout(timeout)
		}
	})
</script>

<div bind:this={node} class="sentinel">
	{#if visible}<div
			class="loading"
			role="status"
			aria-live="polite"
			transition:fly={{
				y: reduceMotion ? 0 : 6,
				duration: reduceMotion ? 120 : 220,
				easing: cubicOut,
			}}
		>
			<span></span>{label}
		</div>{/if}
</div>

<style>
	.sentinel {
		min-height: 1px;
		display: grid;
		place-items: center;
	}
	.loading {
		padding: 2rem;
		display: flex;
		align-items: center;
		gap: 0.7rem;
		color: var(--muted);
		font-size: 0.75rem;
	}
	.loading span {
		width: 18px;
		height: 18px;
		border: 2px solid var(--line);
		border-top-color: var(--ink);
		border-radius: 50%;
		animation: spin 0.7s linear infinite;
	}
	@keyframes spin {
		to {
			transform: rotate(360deg);
		}
	}
	@media (prefers-reduced-motion: reduce) {
		.loading span {
			animation-duration: 1.6s;
		}
	}
</style>
