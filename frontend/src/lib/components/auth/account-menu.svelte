<script lang="ts">
	import Icon from "@components/ui/icon.svelte"
	import { link } from "@lib/actions/link"
	import { navigate } from "@lib/router"
	import { clearSession, currentUser } from "@stores/auth"
	import UserAvatar from "@components/profile/user-avatar.svelte"

	let confirmDialog = $state<HTMLDialogElement>()
	let menu = $state<HTMLDivElement>()
	let loggingOut = $state(false)

	function closeMenu() {
		menu?.hidePopover()
	}

	function askToLogout() {
		closeMenu()
		confirmDialog?.showModal()
	}

	async function confirmLogout() {
		if (loggingOut) return
		loggingOut = true
		try {
			await clearSession()
			confirmDialog?.close()
			navigate("/")
		} finally {
			loggingOut = false
		}
	}
</script>

{#if $currentUser}
	<div class="account">
		<button
			class="account-trigger"
			type="button"
			popovertarget="account-menu"
			aria-label={`Abrir menú de ${$currentUser.firstName}`}
		>
			<span class="initials"><UserAvatar
				seed={$currentUser.avatarSeed}
				initials={`${$currentUser.firstName[0]}${$currentUser.lastName[0]}`}
				name={`${$currentUser.firstName} ${$currentUser.lastName}`}
				size={30}
			/></span>
			<span class="first-name">{$currentUser.firstName}</span>
			<span class="chevron"><Icon name="chevron" size={15} /></span>
		</button>

		<div class="account-popover" id="account-menu" popover="auto" bind:this={menu}>
			<div class="identity">
				<span class="identity-avatar"><UserAvatar
					seed={$currentUser.avatarSeed}
					initials={`${$currentUser.firstName[0]}${$currentUser.lastName[0]}`}
					name={`${$currentUser.firstName} ${$currentUser.lastName}`}
					size={38}
				/></span>
				<div>
					<strong>{$currentUser.firstName} {$currentUser.lastName}</strong>
					<small>{$currentUser.email}</small>
				</div>
			</div>
			<div class="menu-actions">
				<a href="/profile" use:link onclick={closeMenu}>
					<Icon name="user" size={17} />
					<span>Mi perfil</span>
					<span class="shortcut">Ver cuenta</span>
				</a>
				<button type="button" class="logout" onclick={askToLogout}>
					<Icon name="logout" size={17} />
					<span>Cerrar sesión</span>
				</button>
			</div>
		</div>
	</div>

	<dialog
		class="confirm-dialog"
		bind:this={confirmDialog}
		oncancel={(event) => {
			if (loggingOut) event.preventDefault()
		}}
		onclick={(event) => {
			if (event.target === confirmDialog && !loggingOut) confirmDialog?.close()
		}}
	>
		<div class="dialog-icon"><Icon name="logout" size={21} /></div>
		<h2>¿Cerrar sesión?</h2>
		<p>Tendrás que iniciar sesión nuevamente para comentar, valorar y ver tu actividad.</p>
		<div class="dialog-actions">
			<button type="button" disabled={loggingOut} onclick={() => confirmDialog?.close()}>
				Cancelar
			</button>
			<button
				type="button"
				class="confirm"
				disabled={loggingOut}
				onclick={confirmLogout}
				aria-busy={loggingOut}
			>
				{#if loggingOut}<span class="spinner" aria-hidden="true"></span>{/if}
				{loggingOut ? "Cerrando…" : "Sí, cerrar sesión"}
			</button>
		</div>
	</dialog>
{/if}

<style>
	button,
	a {
		font: inherit;
	}

	.account-trigger {
		height: 40px;
		padding: 0.25rem 0.65rem 0.25rem 0.3rem;
		border: 1px solid transparent;
		border-radius: 11px;
		background: var(--ink);
		color: white;
		display: flex;
		align-items: center;
		gap: 0.5rem;
		cursor: pointer;
		transition: background-color 180ms ease-out;
	}

	.account-trigger:hover {
		background: #30312d;
	}

	.account-trigger:focus-visible {
		outline: 3px solid rgba(80, 120, 0, 0.35);
		outline-offset: 2px;
	}

	.initials,
	.identity-avatar {
		display: grid;
		place-items: center;
	}

	.initials {
		width: 30px;
		height: 30px;
		border-radius: 8px;
	}

	.first-name {
		font-size: 0.84rem;
		font-weight: 600;
	}

	.chevron {
		display: grid;
		transform: rotate(90deg);
		opacity: 0.7;
	}

	.account-popover {
		position: fixed;
		inset: 78px max(1rem, calc((100vw - 1280px) / 2 + 1rem)) auto auto;
		width: min(310px, calc(100vw - 2rem));
		margin: 0;
		padding: 0.55rem;
		border: 1px solid var(--line);
		border-radius: 14px;
		background: #fff;
		color: var(--ink);
		box-shadow: 0 8px 8px rgba(25, 26, 23, 0.08);
		opacity: 0;
		transform: translateY(-6px) scale(0.98);
		transition:
			opacity 160ms ease-out,
			transform 160ms ease-out,
			display 160ms allow-discrete,
			overlay 160ms allow-discrete;
	}

	.account-popover:popover-open {
		opacity: 1;
		transform: translateY(0) scale(1);
	}

	@starting-style {
		.account-popover:popover-open {
			opacity: 0;
			transform: translateY(-6px) scale(0.98);
		}
	}

	.identity {
		display: flex;
		align-items: center;
		gap: 0.7rem;
		padding: 0.65rem;
		border-bottom: 1px solid var(--line);
	}

	.identity-avatar {
		width: 38px;
		height: 38px;
		border-radius: 10px;
		flex: 0 0 auto;
	}

	.identity div {
		min-width: 0;
		display: flex;
		flex-direction: column;
	}

	.identity strong {
		font-size: 0.86rem;
	}

	.identity small {
		overflow: hidden;
		color: var(--muted);
		font-size: 0.7rem;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	.menu-actions {
		padding-top: 0.35rem;
	}

	.menu-actions a,
	.menu-actions button {
		width: 100%;
		min-height: 42px;
		padding: 0.65rem 0.7rem;
		border: 0;
		border-radius: 9px;
		background: transparent;
		color: var(--ink);
		display: flex;
		align-items: center;
		gap: 0.65rem;
		text-align: left;
		text-decoration: none;
		cursor: pointer;
	}

	.menu-actions a:hover,
	.menu-actions button:hover {
		background: var(--surface-2);
	}

	.menu-actions span {
		font-size: 0.8rem;
		font-weight: 600;
	}

	.menu-actions .shortcut {
		margin-left: auto;
		color: var(--muted);
		font-size: 0.65rem;
		font-weight: 400;
	}

	.menu-actions .logout {
		color: #8d2c24;
	}

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

	.confirm-dialog h2 {
		margin: 1rem 0 0.45rem;
		font-family: var(--body);
		font-size: 1.2rem;
		letter-spacing: -0.02em;
	}

	.confirm-dialog p {
		margin: 0;
		color: #5f605a;
		font-size: 0.88rem;
		line-height: 1.55;
	}

	.dialog-actions {
		margin-top: 1.4rem;
		display: flex;
		justify-content: flex-end;
		gap: 0.55rem;
	}

	.dialog-actions button {
		min-height: 40px;
		padding: 0.65rem 0.85rem;
		border: 1px solid var(--line);
		border-radius: 9px;
		background: white;
		color: var(--ink);
		cursor: pointer;
	}

	.dialog-actions .confirm {
		border-color: #8d2c24;
		background: #8d2c24;
		color: white;
		display: flex;
		align-items: center;
		gap: 0.45rem;
	}

	.dialog-actions button:disabled {
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
		to {
			transform: rotate(360deg);
		}
	}

	@media (max-width: 720px) {
		.first-name,
		.chevron {
			display: none;
		}
		.account-trigger {
			padding-right: 0.3rem;
		}
	}

	@media (prefers-reduced-motion: reduce) {
		.account-popover {
			transition: none;
		}
		.spinner {
			animation: none;
		}
	}
</style>
