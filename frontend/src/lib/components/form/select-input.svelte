<script lang="ts">
	let {
		label,
		options,
		value = $bindable(""),
	}: {
		label: string
		options: { value: string; label: string }[]
		value?: string
	} = $props()
</script>

<label
	><span>{label}</span><select bind:value
		>{#each options as option}<option value={option.value}
				>{option.label}</option
			>{/each}</select
	></label
>

<style>
	label {
		display: grid;
		gap: 0.45rem;
		font-size: 0.75rem;
		font-weight: 650;
	}
	select {
		width: 100%;
		padding: 0.9rem 2.5rem 0.9rem 1rem;
		border: 1px solid var(--line);
		border-radius: 12px;
		background: white;
		color: var(--ink);
		font: inherit;
		outline: none;
		appearance: none;
		background-image: linear-gradient(45deg, transparent 50%, #777 50%),
			linear-gradient(135deg, #777 50%, transparent 50%);
		background-position:
			calc(100% - 17px) 50%,
			calc(100% - 12px) 50%;
		background-size: 5px 5px;
		background-repeat: no-repeat;
	}
	select:focus {
		border-color: #777;
		box-shadow: 0 0 0 3px rgba(0, 0, 0, 0.05);
	}
</style>
