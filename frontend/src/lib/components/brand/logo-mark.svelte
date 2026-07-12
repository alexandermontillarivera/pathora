<script lang="ts">
	let { size = 30 }: { size?: number } = $props()
</script>

<svg
	width={size}
	height={size}
	viewBox="0 0 64 64"
	aria-label="Pathora"
	role="img"
>
	<rect width="64" height="64" rx="18" fill="#171815" />
	<path
		d="M24 47V17h10.4c7.7 0 12.1 4.1 12.1 10.5 0 6.6-4.6 10.8-12.3 10.8h-4.3V47H24Zm5.9-14h4.2c4.3 0 6.5-1.9 6.5-5.4 0-3.4-2.2-5.3-6.4-5.3h-4.3V33Z"
		fill="#C9FF56"
	/>
</svg>
