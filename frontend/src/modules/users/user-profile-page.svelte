<script lang="ts">
 import CommentThread from "@components/comments/comment-thread.svelte";import CareerCard from "@components/cards/career-card.svelte";import PageLoader from "@components/ui/page-loader.svelte";import PageState from "@components/ui/page-state.svelte";import CountryDisplay from "@components/profile/country-display.svelte";import UserAvatar from "@components/profile/user-avatar.svelte";import Icon from "@components/ui/icon.svelte";import{useUserProfile}from"./use-user-profile.svelte";const page=useUserProfile();let{tab,user,comments,reviewed,loading,error}=$derived(page)
</script>

<svelte:head
	><title
		>{user ? `${user.firstName} ${user.lastName}` : "Perfil"} — Pathora</title
	></svelte:head
>
<main class="page-shell page">
	<button class="back" onclick={() => history.back()}>← Volver</button
	>{#if loading}<PageLoader />{:else if error}<PageState title="No pudimos abrir este perfil." message={error} />{:else if user}<section class="profile-head">
			<div class="identity">
				<div class="avatar"><UserAvatar
					seed={user.avatarSeed}
					initials={`${user.firstName[0]}${user.lastName[0]}`}
					name={`${user.firstName} ${user.lastName}`}
					size={112}
				/></div>
				<div>
					<span>Perfil de la comunidad</span>
					<h1>{user.firstName} {user.lastName}</h1>
					<p>
						<Icon name="world" size={15} /><CountryDisplay country={user.country} /> ·
						Miembro desde {new Date(user.createdAt).getFullYear()}
					</p>
				</div>
			</div>
			<div class="numbers">
				<div><strong>{user.comments ?? 0}</strong><span>comentarios</span></div>
				<div><strong>{user.ratings ?? 0}</strong><span>valoraciones</span></div>
			</div>
		</section>
		<section class="about">
			<span>Sobre {user.firstName}</span>
			<p>
				{user.description || "Esta persona aún no ha escrito una descripción."}
			</p>
		</section>
		<nav>
			<button
				class:active={tab === "activity"}
				onclick={() => (page.tab = "activity")}
				><Icon name="message" size={16} />Actividad</button
			><button
				class:active={tab === "ratings"}
				onclick={() => (page.tab = "ratings")}
				><Icon name="star" size={16} />Valoraciones</button
			>
		</nav>
		{#if tab === "activity"}<section class="content">
				<h2>Lo que {user.firstName} ha compartido.</h2>
				{#if comments.length}<div class="comments">
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
							/>{/each}
					</div>{:else}<div class="empty">
						<h3>Sin comentarios públicos.</h3>
					</div>{/if}
			</section>{:else}<section class="content">
				<h2>Carreras que ha valorado.</h2>
				<div class="grid">
					{#each reviewed as career}<CareerCard {career} />{/each}
				</div>
				{#if !reviewed.length}<div class="empty">
						<h3>Sin valoraciones públicas.</h3>
					</div>{/if}
			</section>{/if}{/if}
</main>

<style>
	.page {
		padding-top: 3rem;
		padding-bottom: 10rem;
	}
	.back {
		border: 0;
		background: none;
		color: var(--muted);
		font: inherit;
		cursor: pointer;
	}
	.profile-head {
		display: flex;
		align-items: center;
		gap: 2rem;
		padding: 5rem 0 3rem;
		border-bottom: 1px solid var(--line);
	}

	.profile-head {
		justify-content: space-between;
	}
	.identity {
		display: flex;
		align-items: center;
		gap: 1.6rem;
	}
	.avatar {
		width: 112px;
		height: 112px;
		display: grid;
		place-items: center;
	}
	.identity span,
	.about > span {
		font-size: 0.66rem;
		text-transform: uppercase;
		letter-spacing: 0.13em;
		color: var(--muted);
	}
	h1 {
		font-family: var(--display);
		font-size: clamp(3rem, 6vw, 5.8rem);
		line-height: 0.9;
		letter-spacing: -0.065em;
		margin: 0.7rem 0 1rem;
	}
	.identity p {
		display: flex;
		gap: 0.4rem;
		color: var(--muted);
	}
	.numbers {
		display: flex;
		gap: 2.5rem;
	}
	.numbers div {
		display: flex;
		flex-direction: column;
	}
	.numbers strong {
		font-family: var(--display);
		font-size: 2.2rem;
	}
	.numbers span {
		font-size: 0.67rem;
		color: var(--muted);
	}
	.about {
		padding: 3rem 0;
	}
	.about p {
		max-width: 760px;
		font-family: var(--display);
		font-size: 1.55rem;
		line-height: 1.45;
	}
	nav {
		display: flex;
		gap: 1.8rem;
		margin: 2rem 0 5rem;
		border-bottom: 1px solid var(--line);
	}
	nav button {
		padding: 1rem 0;
		border: 0;
		border-bottom: 2px solid transparent;
		background: none;
		display: flex;
		gap: 0.4rem;
		color: var(--muted);
		font: inherit;
		cursor: pointer;
	}
	nav button.active {
		border-color: var(--ink);
		color: var(--ink);
	}
	.content h2 {
		font-family: var(--display);
		font-size: clamp(2.7rem, 5vw, 4.5rem);
		letter-spacing: -0.055em;
	}
	.comments {
		max-width: 1000px;
	}
	.grid {
		display: grid;
		grid-template-columns: repeat(3, 1fr);
		gap: 1.2rem;
	}
	.empty {
		padding: 4rem;
		text-align: center;
		border: 1px dashed var(--line);
		border-radius: 22px;
		color: var(--muted);
	}
	@media (max-width: 800px) {
		.profile-head {
			align-items: start;
			flex-direction: column;
		}
		.grid {
			grid-template-columns: 1fr 1fr;
		}
	}
	@media (max-width: 600px) {
		.identity {
			align-items: start;
			flex-direction: column;
		}
		.grid {
			grid-template-columns: 1fr;
		}
		.numbers {
			width: 100%;
			justify-content: space-between;
		}
	}
</style>
