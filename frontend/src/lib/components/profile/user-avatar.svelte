<script lang="ts">
	import { avatarUrl } from "@lib/utils/avatar"

	let {
		seed,
		initials,
		name,
		size = 48,
	}: {
		seed?: string | null
		initials: string
		name: string
		size?: number
	} = $props()

	let failedSeed = $state<string | null>(null)
	let showImage = $derived(Boolean(seed) && failedSeed !== seed)
</script>

<span
	class="user-avatar"
	class:has-image={showImage}
	style={`--avatar-size: ${size}px`}
	aria-hidden="true"
>
	{#if showImage && seed}
		<img
			src={avatarUrl(seed)}
			alt=""
			width={size}
			height={size}
			onerror={() => (failedSeed = seed)}
		/>
	{:else}
		<span>{initials}</span>
	{/if}
</span>

<span class="sr-only">Avatar de {name}</span>

<style>
	.user-avatar {
		width: min(var(--avatar-size), 100%);
		height: auto;
		aspect-ratio: 1;
		display: grid;
		flex: 0 0 auto;
		overflow: hidden;
		place-items: center;
		border-radius: 50%;
		background: var(--accent);
		color: var(--ink);
		font-size: max(0.62rem, calc(var(--avatar-size) * 0.24));
		font-weight: 700;
		line-height: 1;
	}
	.user-avatar.has-image {
		background: transparent;
	}
	img {
		width: 100%;
		height: 100%;
		border-radius: inherit;
		object-fit: cover;
	}
	.sr-only {
		position: absolute;
		width: 1px;
		height: 1px;
		padding: 0;
		margin: -1px;
		overflow: hidden;
		clip: rect(0, 0, 0, 0);
		white-space: nowrap;
		border: 0;
	}
</style>
