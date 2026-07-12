<script lang="ts">
	import SelectInput from "@components/form/select-input.svelte"
	import TextInput from "@components/form/text-input.svelte"
	import { authService } from "@lib/services/auth-service"
	import { userService } from "@lib/services/user-service"
	import TextArea from "@components/form/text-area.svelte"
	import Icon from "@components/ui/icon.svelte"
	import { onMount } from "svelte"

	let firstName = $state("")
	let lastName = $state("")
	let email = $state("")
	let country = $state("DO")
	let description = $state("")
	let saved = $state(false)
	let loading = $state(true)
	let error = $state("")

	const countries = [
		{ value: "DO", label: "República Dominicana" },
		{ value: "MX", label: "México" },
		{ value: "CO", label: "Colombia" },
		{ value: "ES", label: "España" },
		{ value: "AR", label: "Argentina" },
		{ value: "CL", label: "Chile" },
		{ value: "US", label: "Estados Unidos" },
		{ value: "OTHER", label: "Otro país" },
	]

	onMount(async () => {
		try {
			const user = await authService.me()
			firstName = user.firstName
			lastName = user.lastName
			email = user.email ?? ""
			country = user.country ?? "DO"
			description = user.description
		} catch (cause) {
			error =
				cause instanceof Error ? cause.message : "No pudimos cargar tu perfil."
		} finally {
			loading = false
		}
	})
	async function save(event: SubmitEvent) {
		event.preventDefault()
		loading = true
		error = ""
		try {
			await userService.update({ firstName, lastName, description, country })
			saved = true
			setTimeout(() => (saved = false), 2200)
		} catch (cause) {
			error =
				cause instanceof Error
					? cause.message
					: "No pudimos guardar los cambios."
		} finally {
			loading = false
		}
	}
	function logout() {
		authService.logout()
		location.href = "/"
	}
</script>

<div class="settings-layout">
	<form onsubmit={save}>
		<div class="form-head">
			<div>
				<span>Información personal</span>
				<h2>Tu perfil.</h2>
			</div>
			{#if saved}<div class="saved">
					<Icon name="check" size={16} />Cambios guardados
				</div>{/if}
		</div>
		<div class="row">
			<TextInput
				label="Nombre"
				bind:value={firstName}
				autocomplete="given-name"
			/><TextInput
				label="Apellido"
				bind:value={lastName}
				autocomplete="family-name"
			/>
		</div>
		<TextInput
			label="Correo electrónico"
			type="email"
			bind:value={email}
			autocomplete="email"
		/><SelectInput
			label="País"
			bind:value={country}
			options={countries}
		/><TextArea
			label="Sobre ti"
			bind:value={description}
			rows={5}
		/>{#if error}<p class="error" role="alert">{error}</p>{/if}
		<div class="actions">
			<button type="submit" class="primary" disabled={loading}
				>{loading ? "Guardando…" : "Guardar cambios"}</button
			>
		</div>
	</form>
	<aside>
		<section>
			<span>Preferencias</span>
			<h3>Personaliza tu experiencia.</h3>
			<label class="toggle"
				><div>
					<strong>Respuestas</strong><small
						>Cuando alguien responda a tu comentario.</small
					>
				</div>
				<input type="checkbox" checked /><i></i></label
			><label class="toggle"
				><div>
					<strong>Inicios de sesión</strong><small
						>Cuando se acceda desde un dispositivo nuevo.</small
					>
				</div>
				<input type="checkbox" checked /><i></i></label
			>
		</section>
		<section class="danger">
			<span>Cuenta</span><button onclick={logout}
				><Icon name="logout" size={17} />Cerrar sesión</button
			>
		</section>
	</aside>
</div>

<style>
	.settings-layout {
		display: grid;
		grid-template-columns: 1.25fr 0.75fr;
		gap: 1.2rem;
	}
	form,
	aside section {
		padding: 2rem;
		border: 1px solid var(--line);
		border-radius: 22px;
		background: var(--surface);
	}
	.form-head {
		display: flex;
		justify-content: space-between;
		margin-bottom: 2rem;
	}
	.form-head span,
	aside section > span {
		text-transform: uppercase;
		letter-spacing: 0.12em;
		font-size: 0.65rem;
		color: var(--muted);
	}
	h2 {
		font-family: var(--display);
		font-size: 3.5rem;
		margin: 0.8rem 0;
	}
	.saved {
		padding: 0.55rem;
		border-radius: 99px;
		background: #e3f8e8;
		color: #247640;
	}
	.row {
		display: grid;
		grid-template-columns: 1fr 1fr;
		gap: 1rem;
	}
	form > :global(label) {
		margin-bottom: 1rem;
	}
	.actions {
		display: flex;
		justify-content: flex-end;
		margin-top: 1.5rem;
	}
	.primary,
	.danger button {
		padding: 0.8rem 1rem;
		border: 0;
		border-radius: 11px;
		background: var(--ink);
		color: white;
		font: inherit;
	}
	.primary:disabled {
		opacity: 0.5;
	}
	.error {
		color: #9a3024;
	}
	aside {
		display: flex;
		flex-direction: column;
		gap: 1.2rem;
	}
	aside h3 {
		font-family: var(--display);
		font-size: 1.8rem;
	}
	.toggle {
		position: relative;
		display: flex;
		justify-content: space-between;
		padding: 1rem 0;
		border-top: 1px solid var(--line);
	}
	.toggle div {
		display: flex;
		flex-direction: column;
	}
	.toggle small {
		color: var(--muted);
	}
	.toggle input {
		position: absolute;
		opacity: 0;
	}
	.toggle i {
		width: 38px;
		height: 22px;
		padding: 3px;
		border-radius: 99px;
		background: #ddd;
	}
	.toggle i:after {
		content: "";
		display: block;
		width: 16px;
		height: 16px;
		border-radius: 50%;
		background: white;
	}
	.toggle input:checked + i {
		background: var(--ink);
	}
	.toggle input:checked + i:after {
		transform: translateX(16px);
	}
	.danger button {
		width: 100%;
		display: flex;
		gap: 0.5rem;
		background: white;
		color: var(--ink);
		border: 1px solid var(--line);
	}
	@media (max-width: 900px) {
		.settings-layout {
			grid-template-columns: 1fr;
		}
	}
	@media (max-width: 560px) {
		.row {
			grid-template-columns: 1fr;
		}
	}
</style>
