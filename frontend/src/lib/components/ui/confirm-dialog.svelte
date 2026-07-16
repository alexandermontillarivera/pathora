<script lang="ts">
	import Icon from "@components/ui/icon.svelte"

	let {
		title,
		description,
		confirmLabel = "Eliminar",
		pendingLabel = "Eliminando…",
		icon = "trash",
		onConfirm,
	}: {
		title: string
		description: string
		confirmLabel?: string
		pendingLabel?: string
		icon?: string
		onConfirm: () => void | Promise<void>
	} = $props()

	let dialog = $state<HTMLDialogElement>()
	let pending = $state(false)
	let error = $state("")

	export function open() {
		error = ""
		dialog?.showModal()
	}

	function close() {
		if (!pending) dialog?.close()
	}

	async function confirm() {
		if (pending) return
		pending = true
		error = ""
		try {
			await onConfirm()
			dialog?.close()
		} catch (cause) {
			error =
				cause instanceof Error
					? cause.message
					: "No pudimos completar la acción. Inténtalo nuevamente."
		} finally {
			pending = false
		}
	}
</script>

<dialog
	class="confirm-dialog"
	bind:this={dialog}
	oncancel={(event) => {
		if (pending) event.preventDefault()
	}}
	onclick={(event) => {
		if (event.target === dialog) close()
	}}
>
	<div class="dialog-icon"><Icon name={icon} size={21} /></div>
	<h2>{title}</h2>
	<p>{description}</p>
	{#if error}<p class="error" role="alert">{error}</p>{/if}
	<div class="dialog-actions">
		<button type="button" disabled={pending} onclick={close}>Cancelar</button>
		<button
			type="button"
			class="confirm"
			disabled={pending}
			onclick={confirm}
			aria-busy={pending}
		>
			{#if pending}<span class="spinner" aria-hidden="true"></span>{/if}
			{pending ? pendingLabel : confirmLabel}
		</button>
	</div>
</dialog>

<style>
	.confirm-dialog {
		width: min(430px, calc(100vw - 2rem));
		padding: 1.5rem;
		border: 0;
		border-radius: 16px;
		background: white;
		color: var(--ink);
		box-shadow: 0 8px 8px rgba(25, 26, 23, 0.12);
	}
	.confirm-dialog::backdrop {
		background: rgba(17, 18, 15, 0.46);
		backdrop-filter: blur(3px);
	}
	.dialog-icon {
		width: 42px;
		height: 42px;
		border-radius: 11px;
		background: #f5e9e7;
		color: #8d2c24;
		display: grid;
		place-items: center;
	}
	h2 {
		margin: 1rem 0 0.45rem;
		font-family: var(--body);
		font-size: 1.2rem;
		letter-spacing: -0.02em;
		text-wrap: balance;
	}
	p {
		margin: 0;
		color: #5f605a;
		font-size: 0.88rem;
		line-height: 1.55;
		text-wrap: pretty;
	}
	p.error {
		margin-top: 0.75rem;
		color: #8d2c24;
	}
	.dialog-actions {
		margin-top: 1.4rem;
		display: flex;
		justify-content: flex-end;
		gap: 0.55rem;
	}
	button {
		min-height: 40px;
		padding: 0.65rem 0.85rem;
		border: 1px solid var(--line);
		border-radius: 9px;
		background: white;
		color: var(--ink);
		font: inherit;
		cursor: pointer;
	}
	button:focus-visible {
		outline: 3px solid rgba(80, 120, 0, 0.35);
		outline-offset: 2px;
	}
	button.confirm {
		border-color: #8d2c24;
		background: #8d2c24;
		color: white;
		display: flex;
		align-items: center;
		gap: 0.45rem;
	}
	button:disabled {
		cursor: wait;
		opacity: 0.65;
	}
	.spinner {
		width: 13px;
		height: 13px;
		border: 1.5px solid currentColor;
		border-top-color: transparent;
		border-radius: 50%;
		animation: spin 650ms linear infinite;
	}
	@keyframes spin {
		to { transform: rotate(360deg); }
	}
	@media (prefers-reduced-motion: reduce) {
		.spinner { animation: none; }
	}
</style>
