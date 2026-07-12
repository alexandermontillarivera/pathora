<script lang="ts">
	import NotificationCenter from "@components/notifications/notification-center.svelte"
	import { link } from "@dvcol/svelte-simple-router/action"
	import LogoMark from "@components/brand/logo-mark.svelte"
	import { currentUser, clearSession } from "@stores/auth"
	import Icon from "@components/ui/icon.svelte"
	import { openAuthModal } from "@stores/ui"
	import { router } from "@lib/router"

	let open = $state(false)
	let path = $derived(router.location?.path ?? window.location.pathname)

	const items = [
		{ label: "Descubrir", path: "/" },
		{ label: "Escuelas", path: "/schools" },
		{ label: "Comunidad", path: "/community" },
	]
</script>

<header class="header">
	<div class="nav-wrap">
		<a
			class="brand"
			href="/"
			use:link
			onclick={() => (open = false)}
			aria-label="Ir al inicio"><LogoMark /><span>Pathora</span></a
		>
		<nav class:open aria-label="Navegación principal">
			{#each items as item}<a
					class:active={path === item.path}
					href={item.path}
					use:link
					onclick={() => (open = false)}>{item.label}</a
				>{/each}
		</nav>
		<div class="actions">
			{#if !$currentUser}<button class="register" onclick={() => openAuthModal("register")}
					>Registrarse</button
				><button class="login" onclick={() => openAuthModal("login")}
					>Iniciar sesión</button
				>{:else}<button
					class="login"
					onclick={clearSession}
					title="Cerrar sesión">{$currentUser.firstName}</button
				>{/if}{#if $currentUser}<NotificationCenter /><a
				class="avatar"
				href="/profile"
				use:link
				aria-label="Ver perfil"><Icon name="user" size={18} /></a>{/if}<button
				class="mobile-menu"
				onclick={() => (open = !open)}
				aria-label="Abrir menú"><Icon name={open ? "close" : "menu"} /></button
			>
		</div>
	</div>
</header>

<style>
	.header {
		position: sticky;
		top: 0;
		z-index: 50;
		padding: 0.75rem 1rem;
	}
	.nav-wrap {
		max-width: 1280px;
		margin: auto;
		height: 58px;
		padding: 0 0.65rem 0 1rem;
		display: flex;
		align-items: center;
		justify-content: space-between;
		border: 1px solid var(--line);
		border-radius: 18px;
		background: rgba(250, 249, 246, 0.78);
		backdrop-filter: blur(24px) saturate(160%);
		box-shadow: 0 8px 30px rgba(25, 25, 20, 0.06);
	}
	button {
		border: 0;
		background: none;
		color: inherit;
		font: inherit;
		cursor: pointer;
	}
	.brand {
		display: flex;
		align-items: center;
		gap: 0.65rem;
		color: inherit;
		text-decoration: none;
		font-weight: 690;
		letter-spacing: -0.03em;
		font-size: 1.08rem;
	}
	nav {
		display: flex;
		gap: 0.25rem;
	}
	nav a {
		padding: 0.62rem 0.88rem;
		border-radius: 11px;
		color: var(--muted);
		text-decoration: none;
		font-size: 0.9rem;
		transition:
			background 0.18s,
			color 0.18s;
	}
	nav a:hover,
	nav a.active {
		background: rgba(20, 20, 17, 0.06);
		color: var(--ink);
	}
	.actions {
		display: flex;
		align-items: center;
		gap: 0.5rem;
	}
	.login {
		padding: 0.68rem 1rem;
		border-radius: 11px;
		font-size: 0.88rem;
		background: var(--ink);
		color: white;
	}
	.register {
		padding: 0.68rem 0.8rem;
		border-radius: 11px;
		font-size: 0.88rem;
		color: var(--ink);
	}
	.register:hover {
		background: rgba(20, 20, 17, 0.06);
	}
	.avatar {
		width: 38px;
		height: 38px;
		border-radius: 12px;
		display: grid;
		place-items: center;
		background: var(--surface-2);
		color: inherit;
	}
	.mobile-menu {
		display: none;
		width: 40px;
		height: 40px;
		place-items: center;
	}
	@media (max-width: 720px) {
		.login,
		.register,
		.avatar {
			display: none;
		}
		.mobile-menu {
			display: grid;
		}
		nav {
			display: none;
			position: absolute;
			left: 1rem;
			right: 1rem;
			top: 78px;
			padding: 0.5rem;
			flex-direction: column;
			background: rgba(250, 249, 246, 0.96);
			backdrop-filter: blur(24px);
			border: 1px solid var(--line);
			border-radius: 16px;
			box-shadow: var(--shadow);
		}
		nav.open {
			display: flex;
		}
		nav a {
			text-align: left;
			padding: 0.9rem;
		}
	}
</style>
