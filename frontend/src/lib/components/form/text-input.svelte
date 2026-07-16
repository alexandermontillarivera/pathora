<script lang="ts">
	import Icon from "@components/ui/icon.svelte"
	let {
		label,
		type = "text",
		placeholder = "",
		value = $bindable(""),
		required = false,
		readonly = false,
		hint,
		minlength,
		autocomplete,
	}: {
		label: string
		type?: string
		placeholder?: string
		value?: string
		required?: boolean
		readonly?: boolean
		hint?: string
		minlength?: number
		autocomplete?:
			| "given-name"
			| "family-name"
			| "email"
			| "current-password"
			| "new-password"
			| "off"
	} = $props()
	let passwordVisible = $state(false)
	let inputType = $derived(type === "password" && passwordVisible ? "text" : type)
</script>

<label>
	<span>{label}</span>
	<div class="field">
		<input
			bind:value
			type={inputType}
			{placeholder}
			{required}
			{readonly}
			aria-readonly={readonly}
			{minlength}
			{autocomplete}
		/>
		{#if type === "password"}<button
				type="button"
				class="visibility"
				onclick={() => (passwordVisible = !passwordVisible)}
				aria-label={passwordVisible ? "Ocultar contraseña" : "Mostrar contraseña"}
				aria-pressed={passwordVisible}
				title={passwordVisible ? "Ocultar contraseña" : "Mostrar contraseña"}
			><Icon name={passwordVisible ? "eyeOff" : "eye"} size={19} /></button
		>{/if}
	</div>
	{#if hint}<small>{hint}</small>{/if}
</label>

<style>
	label {
		display: grid;
		gap: 0.45rem;
		font-size: 0.75rem;
		font-weight: 650;
	}
	span {
		color: var(--ink);
	}
	.field {
		position: relative;
	}
	input {
		width: 100%;
		box-sizing: border-box;
		padding: 0.9rem 1rem;
		border: 1px solid var(--line);
		border-radius: 12px;
		background: white;
		color: var(--ink);
		font: inherit;
		outline: none;
		transition:
			border-color 0.18s,
			box-shadow 0.18s;
	}
	input:focus {
		border-color: #777;
		box-shadow: 0 0 0 3px rgba(0, 0, 0, 0.05);
	}
	input:read-only {
		background: var(--surface-2);
		color: var(--muted);
		cursor: text;
	}
	input:read-only:focus {
		border-color: var(--line);
		box-shadow: none;
	}
	small {
		color: var(--muted);
		font-size: 0.7rem;
		font-weight: 450;
		line-height: 1.45;
	}
	input[type="password"],
	.field:has(.visibility) input {
		padding-right: 3rem;
	}
	.visibility {
		position: absolute;
		right: 0.55rem;
		top: 50%;
		width: 36px;
		height: 36px;
		transform: translateY(-50%);
		border: 0;
		border-radius: 9px;
		background: transparent;
		color: var(--muted);
		display: grid;
		place-items: center;
		cursor: pointer;
	}
	.visibility:hover {
		background: var(--surface-2);
		color: var(--ink);
	}
	.visibility:focus-visible {
		outline: 2px solid var(--ink);
		outline-offset: 1px;
	}
</style>
