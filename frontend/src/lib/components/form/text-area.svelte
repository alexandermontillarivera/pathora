<script lang="ts">
	let {
		label,
		placeholder = "",
		value = $bindable(""),
		rows = 5,
	}: {
		label: string
		placeholder?: string
		value?: string
		rows?: number
	} = $props()
</script>

<label
	><span>{label}</span><textarea bind:value {placeholder} {rows}
	></textarea></label
>

<style>
	label {
		display: grid;
		gap: 0.45rem;
		font-size: 0.75rem;
		font-weight: 650;
	}
	textarea {
		width: 100%;
		resize: vertical;
		padding: 0.9rem 1rem;
		border: 1px solid var(--line);
		border-radius: 12px;
		background: white;
		color: var(--ink);
		font: inherit;
		line-height: 1.6;
		outline: none;
		transition:
			border-color 0.18s,
			box-shadow 0.18s;
	}
	textarea:focus {
		border-color: #777;
		box-shadow: 0 0 0 3px rgba(0, 0, 0, 0.05);
	}
</style>
