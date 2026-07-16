<script lang="ts">
	import Icon from "@components/ui/icon.svelte"
	import UserAvatar from "@components/profile/user-avatar.svelte"
	import { avatarUrl, TAPBACK_AVATAR_SEEDS } from "@lib/utils/avatar"

	let {
		value = $bindable<string | undefined>(),
		firstName,
		lastName,
	}: {
		value?: string
		firstName: string
		lastName: string
	} = $props()

	let expanded = $state(false)
	let initials = $derived(`${firstName[0] ?? ""}${lastName[0] ?? ""}`)
	let fullName = $derived(`${firstName} ${lastName}`.trim() || "tu perfil")

	function select(seed?: string) {
		value = seed
		expanded = false
	}
</script>

<fieldset class:expanded>
	<legend>Imagen de perfil</legend>
	<div class="current">
		<UserAvatar seed={value} {initials} name={fullName} size={88} />
		<div>
			<strong>{value ? "Tu Memoji" : "Tus iniciales"}</strong>
			<p>Elige una imagen que te represente en Pathora.</p>
			<button
				type="button"
				class="change"
				onclick={() => (expanded = !expanded)}
				aria-expanded={expanded}
				aria-controls="profile-avatar-options"
			>
				<Icon name={expanded ? "close" : "edit"} size={15} />
				{expanded ? "Cerrar selector" : "Cambiar imagen"}
			</button>
		</div>
	</div>

	{#if expanded}
		<div class="picker" id="profile-avatar-options">
			<div class="picker-head">
				<div>
					<strong>Escoge entre {TAPBACK_AVATAR_SEEDS.length} avatares</strong>
					<span>La selección se aplicará cuando guardes el perfil.</span>
				</div>
				{#if value}<button type="button" class="initials-option" onclick={() => select()}>
						Usar iniciales
					</button>{/if}
			</div>
			<div class="options" role="group" aria-label="Avatares disponibles">
				{#each TAPBACK_AVATAR_SEEDS as seed, index}
					<button
						type="button"
						class:selected={value === seed}
						aria-pressed={value === seed}
						aria-label={`Seleccionar avatar ${index + 1}`}
						onclick={() => select(seed)}
					>
						<img
							src={avatarUrl(seed)}
							alt=""
							width="72"
							height="72"
							loading="lazy"
						/>
						<span class="check"><Icon name="check" size={13} /></span>
					</button>
				{/each}
			</div>
			<p class="credit">
				Avatares generados por <a
					href="https://www.tapback.co"
					target="_blank"
					rel="noreferrer">Tapback</a
				>.
			</p>
		</div>
	{/if}
</fieldset>

<style>
	fieldset {
		min-width: 0;
		margin: 0 0 1.25rem;
		padding: 0 0 1.25rem;
		border: 0;
		border-bottom: 1px solid var(--line);
	}
	legend {
		margin-bottom: 0.65rem;
		padding: 0;
		font-size: 0.75rem;
		font-weight: 650;
	}
	.current {
		display: flex;
		align-items: center;
		gap: 1rem;
	}
	.current > div {
		min-width: 0;
	}
	.current strong,
	.picker-head strong {
		display: block;
		font-size: 0.88rem;
	}
	.current p {
		margin: 0.25rem 0 0.7rem;
		color: var(--muted);
		font-size: 0.76rem;
		line-height: 1.45;
	}
	button {
		font: inherit;
		cursor: pointer;
	}
	.change,
	.initials-option {
		min-height: 34px;
		padding: 0.45rem 0.65rem;
		border: 1px solid var(--line);
		border-radius: 9px;
		background: white;
		color: var(--ink);
		font-size: 0.72rem;
		font-weight: 600;
	}
	.change {
		display: inline-flex;
		align-items: center;
		gap: 0.4rem;
	}
	.change:hover,
	.initials-option:hover {
		background: var(--surface-2);
	}
	.picker {
		margin-top: 1rem;
		padding-top: 1rem;
		border-top: 1px solid var(--line);
		animation: reveal 180ms cubic-bezier(0.22, 1, 0.36, 1);
	}
	.picker-head {
		display: flex;
		align-items: center;
		justify-content: space-between;
		gap: 1rem;
		margin-bottom: 0.85rem;
	}
	.picker-head span {
		display: block;
		margin-top: 0.2rem;
		color: var(--muted);
		font-size: 0.7rem;
	}
	.options {
		display: grid;
		grid-template-columns: repeat(auto-fill, minmax(64px, 1fr));
		gap: 0.55rem;
	}
	.options button {
		position: relative;
		aspect-ratio: 1;
		min-width: 0;
		padding: 3px;
		border: 2px solid transparent;
		border-radius: 50%;
		background: transparent;
		transition:
			border-color 160ms ease-out,
			transform 160ms ease-out;
	}
	.options button:hover {
		transform: translateY(-2px);
	}
	.options button.selected {
		border-color: var(--ink);
	}
	.options img {
		width: 100%;
		height: 100%;
		border-radius: 50%;
		object-fit: cover;
	}
	.check {
		position: absolute;
		right: -4px;
		bottom: -4px;
		width: 23px;
		height: 23px;
		border: 2px solid white;
		border-radius: 50%;
		background: var(--ink);
		color: white;
		display: none;
		place-items: center;
	}
	.selected .check {
		display: grid;
	}
	.credit {
		margin: 0.75rem 0 0;
		color: var(--muted);
		font-size: 0.65rem;
	}
	.credit a {
		color: inherit;
	}
	@keyframes reveal {
		from {
			opacity: 0;
			transform: translateY(-4px);
		}
	}
	@media (max-width: 560px) {
		.current {
			align-items: flex-start;
		}
		.picker-head {
			align-items: flex-start;
			flex-direction: column;
		}
	}
	@media (prefers-reduced-motion: reduce) {
		.picker {
			animation: none;
		}
		.options button {
			transition: none;
		}
	}
</style>
