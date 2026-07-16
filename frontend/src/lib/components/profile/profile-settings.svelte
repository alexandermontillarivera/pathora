<script lang="ts">
	import SelectInput from "@components/form/select-input.svelte"
	import TextInput from "@components/form/text-input.svelte"
	import { authService } from "@lib/services/auth-service"
	import { userService } from "@lib/services/user-service"
	import TextArea from "@components/form/text-area.svelte"
	import Icon from "@components/ui/icon.svelte"
	import LocalAiSettings from "@components/ai/local-ai-settings.svelte"
	import { onMount } from "svelte"
	import { countryOptions } from "@lib/utils/countries"
	import { mergeCurrentUser } from "@stores/auth"
	import ProfileImagePicker from "@components/profile/profile-image-picker.svelte"
	import ConfirmDialog from "@components/ui/confirm-dialog.svelte"
	import { clearSession } from "@stores/auth"
	import { clearSavedCareerIds } from "@stores/saved-careers"
	import { navigate } from "@lib/router"

	let firstName = $state("")
	let lastName = $state("")
	let email = $state("")
	let country = $state("")
	let description = $state("")
	let avatarSeed = $state<string | undefined>()
	let saved = $state(false)
	let loading = $state(true)
	let error = $state("")
	let deleteDialog = $state<ConfirmDialog>()

	onMount(async () => {
		try {
			const user = await authService.me()
			firstName = user.firstName
			lastName = user.lastName
			email = user.email ?? ""
			country = user.country ?? ""
			description = user.description
			avatarSeed = user.avatarSeed
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
			const updatedUser = await userService.update({
				firstName,
				lastName,
				description,
				country: country || undefined,
				avatarSeed,
			})
			mergeCurrentUser(updatedUser)
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
	async function deleteAccount() {
		await userService.removeCurrent()
		clearSavedCareerIds()
		await clearSession()
		navigate("/")
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
		<ProfileImagePicker
			bind:value={avatarSeed}
			{firstName}
			{lastName}
		/>
		<div class="row name-fields">
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
			readonly
			hint="El correo identifica tu cuenta y no se puede modificar."
		/><SelectInput
			label="País"
			bind:value={country}
			options={countryOptions}
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
		<section>
			<span>Inteligencia local</span>
			<LocalAiSettings />
		</section>
		<section class="danger">
			<span>Zona de riesgo</span>
			<h3>Eliminar tu cuenta.</h3>
			<p>Esta acción borra permanentemente tu actividad y no se puede deshacer.</p>
			<button onclick={() => deleteDialog?.open()}
				><Icon name="trash" size={17} />Eliminar cuenta</button
			>
		</section>
	</aside>
</div>

<ConfirmDialog
	bind:this={deleteDialog}
	title="¿Eliminar tu cuenta definitivamente?"
	description="Se eliminarán tu perfil, comentarios, respuestas, valoraciones, votos, carreras guardadas, notificaciones y sesiones activas. Esta acción no se puede deshacer."
	confirmLabel="Sí, eliminar mi cuenta"
	pendingLabel="Eliminando cuenta…"
	icon="trash"
	onConfirm={deleteAccount}
/>

<style>
	.settings-layout {
		display: grid;
		grid-template-columns: 1.25fr 0.75fr;
		gap: 1.2rem;
		align-items: start;
	}

  .name-fields {
    margin-bottom: 1rem;
  }

	form {
		height: fit-content;
		align-self: start;
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
		align-items: flex-start;
		justify-content: space-between;
		gap: 1rem;
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
		display: inline-flex;
		align-items: center;
		align-self: flex-start;
		gap: 0.35rem;
		flex: 0 0 auto;
		padding: 0.5rem 0.7rem;
		border-radius: 99px;
		background: #e3f8e8;
		color: #247640;
		font-size: 0.78rem;
		font-weight: 600;
		line-height: 1;
		white-space: nowrap;
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
		justify-content: center;
		background: #8d2c24;
		color: white;
		border: 1px solid #8d2c24;
		cursor: pointer;
	}
	.danger p {
		margin: -0.35rem 0 1rem;
		color: var(--muted);
		font-size: 0.78rem;
		line-height: 1.55;
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
