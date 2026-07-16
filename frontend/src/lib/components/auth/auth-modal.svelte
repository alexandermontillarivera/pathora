<script lang="ts">
	import TextInput from "@components/form/text-input.svelte"
	import { authService } from "@lib/services/auth-service"
	import { currentUser } from "@stores/auth"
	import type { AuthModalMode } from "@stores/ui"
	import Icon from "@components/ui/icon.svelte"
	import SelectInput from "@components/form/select-input.svelte"
	import { countryOptions, detectCountry } from "@lib/utils/countries"

	let {
		onClose,
		initialMode = "login",
	}: { onClose: () => void; initialMode?: AuthModalMode } = $props()
	type Mode = "login" | "register" | "forgot"

	let mode = $state<Mode>("login")
	$effect(() => {
		mode = initialMode
	})
	let complete = $state(false)
	let loading = $state(false)
	let error = $state("")
	let firstName = $state("")
	let lastName = $state("")
	let email = $state("")
	let password = $state("")
	let country = $state(detectCountry())

	async function submit() {
		loading = true
		error = ""
		try {
			if (mode === "forgot") {
				await authService.forgotPassword(email)
			} else if (mode === "login") {
				currentUser.set((await authService.login(email, password)).user)
			} else {
				currentUser.set(
					(await authService.register({
						firstName,
						lastName,
						email,
						password,
						country: country || undefined,
					}))
						.user,
				)
			}
			complete = true
		} catch (cause) {
			error =
				cause instanceof Error
					? cause.message
					: "No pudimos completar la solicitud."
		} finally {
			loading = false
		}
	}
	function change(next: Mode) {
		mode = next
		complete = false
		error = ""
	}
</script>

<div
	class="scrim"
	role="presentation"
	onclick={(event) => event.target === event.currentTarget && onClose()}
>
	<div
		class="modal"
		role="dialog"
		aria-modal="true"
		aria-labelledby="auth-title"
	>
		<button class="close" onclick={onClose} aria-label="Cerrar"
			><Icon name="close" /></button
		>
		{#if complete}
			<div class="success">
				<span
					><Icon name={mode === "forgot" ? "send" : "check"} size={28} /></span
				>
				<h2 id="auth-title">
					{mode === "forgot" ? "Revisa tu correo." : "Ya estás dentro."}
				</h2>
				<p>
					{mode === "forgot"
						? "Si existe una cuenta asociada, recibirás un enlace válido durante 30 minutos."
						: "Tu sesión está lista. Continúa explorando y guarda lo que te interese."}
				</p>
				{#if mode === "forgot"}
					<button class="primary" onclick={() => change("login")}
						>Volver a iniciar sesión
					</button>
				{:else}
					<button class="primary" onclick={onClose}>Explorar Pathora </button>
				{/if}
			</div>
		{:else}
			<span class="kicker"
				>{mode === "forgot" ? "Recuperar acceso" : "Tu espacio"}</span
			>
			<h2 id="auth-title">
				{mode === "login"
					? "Continúa tu camino."
					: mode === "register"
						? "Empieza a descubrir."
						: "Volvamos a encontrarte."}
			</h2>
			<p class="intro">
				{mode === "forgot"
					? "Escribe tu correo y te enviaremos un enlace seguro para crear una contraseña nueva."
					: "Guarda carreras, comparte tu experiencia y encuentra una ruta que se sienta tuya."}
			</p>
			<form
				onsubmit={(event) => {
					event.preventDefault()
					submit()
				}}
			>
				{#if mode === "register"}<div class="row">
						<TextInput
							label="Nombre"
							placeholder="Ana"
							bind:value={firstName}
							required
							autocomplete="given-name"
						/><TextInput
							label="Apellido"
							placeholder="Pérez"
							bind:value={lastName}
							required
							autocomplete="family-name"
						/>
					</div>{/if}
				{#if mode === "register"}<SelectInput
						label="País"
						bind:value={country}
						options={countryOptions}
					/>{/if}
				<TextInput
					label="Correo"
					type="email"
					placeholder="tu@correo.com"
					bind:value={email}
					required
					autocomplete="email"
				/>
				{#if mode !== "forgot"}<TextInput
						label="Contraseña"
						type="password"
						placeholder="Mínimo 8 caracteres"
						bind:value={password}
						required
						minlength={8}
						autocomplete={mode === "login"
							? "current-password"
							: "new-password"}
					/>{/if}
				{#if error}<p class="error" role="alert">{error}</p>{/if}
				{#if mode === "login"}<button
						class="forgot"
						type="button"
						onclick={() => change("forgot")}>¿Olvidaste tu contraseña?</button
					>{/if}
				<button class="primary" type="submit" disabled={loading}
					>{loading
						? "Procesando…"
						: mode === "login"
							? "Iniciar sesión"
							: mode === "register"
								? "Crear mi cuenta"
								: "Enviar enlace"}
					<Icon name="arrow" size={17} /></button
				>
			</form>
			{#if mode !== "forgot"}<button
					class="switch"
					onclick={() => change(mode === "login" ? "register" : "login")}
					>{mode === "login"
						? "¿Aún no tienes cuenta? Crear una"
						: "Ya tengo una cuenta"}</button
				>{:else}<button class="switch" onclick={() => change("login")}
					>← Volver a iniciar sesión</button
				>{/if}
		{/if}
	</div>
</div>

<style>
	.scrim {
		position: fixed;
		inset: 0;
		z-index: 100;
		background: rgba(20, 20, 17, 0.38);
		backdrop-filter: blur(12px);
		display: grid;
		place-items: center;
		padding: 1rem;
		overscroll-behavior: contain;
	}
	.modal {
		width: min(470px, 100%);
		position: relative;
		padding: 2.4rem;
		background: rgba(253, 252, 249, 0.97);
		border: 1px solid rgba(255, 255, 255, 0.7);
		border-radius: 28px;
		box-shadow: 0 30px 90px rgba(0, 0, 0, 0.2);
		animation: materialize 0.35s cubic-bezier(0.2, 0.8, 0.2, 1);
	}
	button {
		font: inherit;
		cursor: pointer;
	}
	.close {
		position: absolute;
		right: 1rem;
		top: 1rem;
		width: 40px;
		height: 40px;
		border: 0;
		border-radius: 50%;
		display: grid;
		place-items: center;
		background: var(--surface-2);
	}
	.kicker {
		font-size: 0.72rem;
		text-transform: uppercase;
		letter-spacing: 0.12em;
		color: var(--muted);
	}
	h2 {
		font-family: var(--display);
		font-size: 2.5rem;
		line-height: 1;
		letter-spacing: -0.05em;
		margin: 1.2rem 0;
	}
	.intro {
		color: var(--muted);
		line-height: 1.6;
		margin-bottom: 2rem;
	}
	form {
		display: grid;
		gap: 1rem;
	}
	.row {
		display: grid;
		grid-template-columns: 1fr 1fr;
		gap: 0.75rem;
	}
	.primary {
		width: 100%;
		margin-top: 0.5rem;
		border: 0;
		margin-inline: 0;
		border-radius: 13px;
		padding: 1rem 1.1rem;
		background: var(--ink);
		color: white;
		display: flex;
		align-items: center;
		justify-content: center;
		gap: 0.5rem;
		font-weight: 650;
	}
	.primary:disabled {
		opacity: 0.55;
		cursor: wait;
	}
	.forgot {
		justify-self: end;
		border: 0;
		background: none;
		padding: 0;
		color: var(--muted);
		font-size: 0.75rem;
	}
	.switch {
		width: 100%;
		margin-top: 1.2rem;
		border: 0;
		background: none;
		color: var(--muted);
		font-size: 0.82rem;
	}
	.error {
		margin: 0;
		padding: 0.7rem;
		border-radius: 10px;
		background: #fff0ed;
		color: #9a3024;
		font-size: 0.75rem;
	}
	.success {
		text-align: center;
		padding: 2rem 0.5rem;
	}
	.success > span {
		margin: auto;
		width: 64px;
		height: 64px;
		border-radius: 50%;
		background: var(--accent);
		display: grid;
		place-items: center;
	}
	.success p {
		color: var(--muted);
		line-height: 1.6;
	}
	@keyframes materialize {
		from {
			opacity: 0;
			transform: scale(0.94);
			filter: blur(10px);
		}
		to {
			opacity: 1;
			transform: none;
			filter: none;
		}
	}
	@media (max-width: 520px) {
		.modal {
			padding: 2rem 1.3rem;
		}
		.row {
			grid-template-columns: 1fr;
		}
	}
	@media (prefers-reduced-motion: reduce) {
		.modal {
			animation: none;
		}
	}
</style>
