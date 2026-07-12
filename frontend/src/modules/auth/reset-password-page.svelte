<script lang="ts">
	import { useResetPasswordPage } from "./use-reset-password.svelte"
	import TextInput from "@components/form/text-input.svelte"
	import { link } from "@dvcol/svelte-simple-router/action"
	import Icon from "@components/ui/icon.svelte"
	const page = useResetPasswordPage()
</script>

<svelte:head><title>Nueva contraseña — Pathora</title></svelte:head>
<main class="page-shell">
	<section>
		<div class="mark">P</div>
		{#if page.complete}<span>Acceso recuperado</span>
			<h1>Tu nueva contraseña está lista.</h1>
			<p>
				Ya puedes volver a Pathora e iniciar sesión con tus nuevas credenciales.
			</p>
			<a href="/" use:link>Volver a Pathora <Icon name="arrow" size={17} /></a
			>{:else}<span>Seguridad de la cuenta</span>
			<h1>Crea una nueva contraseña.</h1>
			<p>
				Elige una contraseña única de al menos 8 caracteres que no utilices en
				otros servicios.
			</p>
			<form
				onsubmit={(event) => {
					event.preventDefault()
					page.submit()
				}}
			>
				<TextInput
					label="Nueva contraseña"
					type="password"
					bind:value={page.form.password}
					minlength={8}
					required
					autocomplete="new-password"
				/><TextInput
					label="Confirmar contraseña"
					type="password"
					bind:value={page.form.confirmation}
					minlength={8}
					required
					autocomplete="new-password"
				/>{#if page.error}<div class="error" role="alert">
						{page.error}
					</div>{/if}<button disabled={page.loading || !page.token}
					>{page.loading ? "Actualizando…" : "Cambiar contraseña"}
					<Icon name="arrow" size={17} /></button
				>
			</form>{/if}
	</section>
</main>

<style>
	main {
		min-height: 75vh;
		display: grid;
		place-items: center;
		padding-top: 5rem;
		padding-bottom: 8rem;
	}
	section {
		width: min(540px, 100%);
		padding: 3rem;
		border: 1px solid var(--line);
		border-radius: 28px;
		background: var(--surface);
	}
	.mark {
		width: 46px;
		height: 46px;
		border-radius: 13px;
		background: var(--ink);
		color: var(--accent);
		display: grid;
		place-items: center;
		font-family: var(--display);
		font-size: 1.5rem;
		margin-bottom: 3rem;
	}
	section > span {
		text-transform: uppercase;
		letter-spacing: 0.13em;
		font-size: 0.66rem;
		color: var(--muted);
	}
	h1 {
		font-family: var(--display);
		font-size: 3.3rem;
		line-height: 0.95;
		letter-spacing: -0.055em;
		margin: 1rem 0;
	}
	p {
		color: var(--muted);
		line-height: 1.65;
		margin-bottom: 2rem;
	}
	form {
		display: grid;
		gap: 1rem;
	}
	form button,
	a {
		border: 0;
		border-radius: 12px;
		padding: 1rem;
		background: var(--ink);
		color: white;
		display: flex;
		align-items: center;
		justify-content: center;
		gap: 0.5rem;
		font: inherit;
		font-weight: 650;
		text-decoration: none;
		cursor: pointer;
	}
	button:disabled {
		opacity: 0.5;
	}
	.error {
		padding: 0.8rem;
		border-radius: 10px;
		background: #fff0ed;
		color: #9a3024;
		font-size: 0.75rem;
	}
	@media (max-width: 600px) {
		section {
			padding: 2rem 1.3rem;
		}
	}
</style>
