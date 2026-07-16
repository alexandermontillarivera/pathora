<script lang="ts">
	import { countryName } from "@lib/utils/countries"

	let { country }: { country?: string | null } = $props()
	let code = $derived(country?.trim().toLowerCase() ?? "")
	let hasFlag = $derived(/^[a-z]{2}$/.test(code))
	let failedCode = $state("")
</script>

<span class="country">
	{#if hasFlag && failedCode !== code}<img
			src={`https://flagcdn.com/${code}.svg`}
			alt=""
			width="20"
			height="15"
			loading="lazy"
			onerror={() => (failedCode = code)}
		/>{/if}
	<span>{countryName(country)}</span>
</span>

<style>
	.country {
		display: inline-flex;
		align-items: center;
		gap: 0.42rem;
	}
	img {
		width: 1.15rem;
		height: auto;
		aspect-ratio: 4 / 3;
		object-fit: cover;
		border-radius: 2px;
		box-shadow: 0 0 0 1px rgba(25, 26, 23, 0.1);
	}
</style>
