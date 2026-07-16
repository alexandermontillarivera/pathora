<script lang="ts">
 import ProfileSettings from "@components/profile/profile-settings.svelte";import CountryDisplay from "@components/profile/country-display.svelte";import UserAvatar from "@components/profile/user-avatar.svelte";import CommentThread from "@components/comments/comment-thread.svelte";import CareerCard from "@components/cards/career-card.svelte";import PageLoader from "@components/ui/page-loader.svelte";import ConfirmDialog from "@components/ui/confirm-dialog.svelte";import Icon from "@components/ui/icon.svelte";import{openAuthModal}from"@stores/ui";import{currentUser}from"@stores/auth";import{useProfile,type ProfileTab}from"./use-profile.svelte";const page=useProfile();let{tab,user,saved,comments,ratings,loading,error}=$derived(page);let displayUser=$derived($currentUser??user);let ratingDialog=$state<ConfirmDialog>();let ratingToDelete=$state<{id:number;name:string}>();function askToRemoveRating(id:number,name:string){ratingToDelete={id,name};ratingDialog?.open()}async function confirmRatingRemoval(){if(ratingToDelete)await page.removeRating(ratingToDelete.id)}
</script>

<svelte:head>
	<title>Mi perfil — Pathora</title>
</svelte:head>
<main class="page-shell page">
	{#if loading}<PageLoader />
	{:else if displayUser}
		<section class="summary">
			<div class="avatar"><UserAvatar
				seed={displayUser.avatarSeed}
				initials={`${displayUser.firstName[0]}${displayUser.lastName[0]}`}
				name={`${displayUser.firstName} ${displayUser.lastName}`}
				size={112}
			/></div>
			<div>
				<span>Tu espacio personal</span>
				<h1>{displayUser.firstName} {displayUser.lastName}</h1>
				<p>
					{displayUser.description ||
						"Completa tu descripción para que la comunidad te conozca."}
				</p>
				<small
					><CountryDisplay country={displayUser.country} /> · Miembro desde {new Date(
						displayUser.createdAt,
					).getFullYear()}</small
				>
			</div>
			<button onclick={() => (page.tab = "settings")}
				><Icon name="edit" size={16} />Editar perfil</button
			>
		</section>
		<nav>
			{#each [{ id: "saved", label: "Guardadas", icon: "bookmark", count: saved.length }, { id: "comments", label: "Comentarios", icon: "message", count: comments.length }, { id: "ratings", label: "Valoraciones", icon: "star", count: ratings.length }, { id: "settings", label: "Ajustes", icon: "settings" }] as item}<button
					class:active={tab === item.id}
					onclick={() => (page.tab = item.id as ProfileTab)}
					><Icon
						name={item.icon}
						size={16}
					/>{item.label}{#if item.count !== undefined}<span>{item.count}</span
						>{/if}</button
				>{/each}
		</nav>
		{#if tab === "saved"}<section>
				<h2>Carreras guardadas.</h2>
				<div class="grid">
					{#each saved as career}<CareerCard {career} initiallySaved onRemoved={page.removeSaved} />{/each}
				</div>
				{#if !saved.length}<div class="empty">
						<h3>Aún no guardas carreras.</h3>
					</div>{/if}
			</section>
		{:else if tab === "comments"}<section>
				<h2>Tus comentarios.</h2>
				<div class="comments">
					{#each comments as item}<CommentThread
							comment={{
								id: item.id,
								userId: item.author.id,
								name: `${item.author.firstName} ${item.author.lastName}`,
								initials: `${item.author.firstName[0]}${item.author.lastName[0]}`,
								avatarSeed: item.author.avatarSeed,
								time: new Intl.DateTimeFormat("es", {
									dateStyle: "medium",
								}).format(new Date(item.createdAt)),
								content: item.content,
								rating: 5,
							}}
							career={item.career.name}
							careerId={item.career.id}
							useful={item.useful}
							notUseful={item.notUseful}
							currentVote={item.currentVote}
							onDeleted={page.removeComment}
						/>{/each}
				</div>
				{#if !comments.length}<div class="empty">
						<h3>Aún no has comentado.</h3>
					</div>{/if}
			</section>
		{:else if tab === "ratings"}<section>
				<h2>Tus valoraciones.</h2>
				<div class="rating-list">
					{#each ratings as rating}<article>
							<Icon name="star" />
							<div>
								<strong>{rating.career.name}</strong>
								<p>{"★".repeat(rating.value)}{"☆".repeat(5 - rating.value)}</p>
							</div>
							<button
								aria-label="Eliminar valoración"
								onclick={() => askToRemoveRating(rating.id, rating.career.name)}><Icon name="trash" size={15} /></button
							>
						</article>{/each}
				</div>
				{#if !ratings.length}<div class="empty">
						<h3>Aún no has valorado carreras.</h3>
					</div>{/if}
			</section>
		{:else}<ProfileSettings />{/if}
	{:else}<div class="empty" role="alert">
			<h2>Inicia sesión para ver tu perfil.</h2>
			<p>{error}</p>
			<button onclick={openAuthModal}>Iniciar sesión</button>
		</div>{/if}
</main>

<ConfirmDialog
	bind:this={ratingDialog}
	title="¿Eliminar valoración?"
	description={`Tu valoración de ${ratingToDelete?.name ?? "esta carrera"} desaparecerá y dejará de contar en su promedio.`}
	confirmLabel="Sí, eliminar valoración"
	onConfirm={confirmRatingRemoval}
/>

<style>
	.page {
		padding-top: 4rem;
		padding-bottom: 9rem;
	}

	.summary {
		display: grid;
		grid-template-columns: 112px 1fr auto;
		align-items: center;
		gap: 1.6rem;
		padding: 2rem;
		border: 1px solid var(--line);
		border-radius: 28px;
		background: var(--surface);
	}
	.avatar {
		width: 112px;
		height: 112px;
		display: grid;
		place-items: center;
	}
	.summary span {
		font-size: 0.66rem;
		text-transform: uppercase;
		letter-spacing: 0.12em;
		color: var(--muted);
	}
	h1 {
		font-family: var(--display);
		font-size: 3.5rem;
		line-height: 1;
		margin: 0.5rem 0;
	}
	.summary p,
	.summary small {
		color: var(--muted);
	}
	.summary button,
	.rating-list button,
	.empty button {
		border: 1px solid var(--line);
		border-radius: 10px;
		background: white;
		padding: 0.7rem;
		display: flex;
		gap: 0.4rem;
		font: inherit;
		cursor: pointer;
	}
	nav {
		display: flex;
		gap: 1.8rem;
		margin: 3rem 0 6rem;
		border-bottom: 1px solid var(--line);
		overflow: auto;
	}
	nav button {
		padding: 1rem 0;
		border: 0;
		border-bottom: 2px solid transparent;
		background: none;
		display: flex;
		align-items: center;
		gap: 0.4rem;
		color: var(--muted);
		font: inherit;
		white-space: nowrap;
	}
	nav button.active {
		color: var(--ink);
		border-color: var(--ink);
	}
	nav span {
		background: var(--surface-2);
		border-radius: 99px;
		padding: 0.15rem 0.4rem;
	}
	h2 {
		font-family: var(--display);
		font-size: clamp(3rem, 6vw, 5rem);
		letter-spacing: -0.06em;
	}
	.grid {
		display: grid;
		grid-template-columns: repeat(3, 1fr);
		gap: 1.2rem;
	}
	.comments {
		max-width: 1000px;
	}
	.rating-list {
		max-width: 800px;
	}
	.rating-list article {
		display: flex;
		align-items: center;
		gap: 1rem;
		padding: 1.2rem 0;
		border-bottom: 1px solid var(--line);
	}
	.rating-list article > div {
		flex: 1;
	}
	.rating-list p {
		color: #d29a00;
	}
	.empty {
		padding: 4rem;
		text-align: center;
		border: 1px dashed var(--line);
		border-radius: 22px;
		color: var(--muted);
	}
	.empty button {
		margin: auto;
		background: var(--ink);
		color: white;
	}
	@media (max-width: 800px) {
		.summary {
			grid-template-columns: 80px 1fr;
		}
		.avatar {
			width: 80px;
			height: 80px;
		}
		.grid {
			grid-template-columns: 1fr 1fr;
		}
	}
	@media (max-width: 560px) {
		.grid {
			grid-template-columns: 1fr;
		}
		.summary {
			grid-template-columns: 1fr;
		}
	}
</style>
