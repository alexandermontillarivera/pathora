<script lang="ts">
 import CommentThread from "@components/comments/comment-thread.svelte";import RichTextEditor from "@components/form/rich-text-editor.svelte";import RichContent from "@components/ui/rich-content.svelte";import PageLoader from "@components/ui/page-loader.svelte";import PageState from "@components/ui/page-state.svelte";import ConfirmDialog from "@components/ui/confirm-dialog.svelte";import{markdownSummary}from"@lib/utils/markdown";import{currentUser}from"@stores/auth";import Icon from "@components/ui/icon.svelte";import{useCareer,type CareerTab}from"./use-career.svelte";const page=useCareer();let{career,apiPensum,rating,loading,loadError,loadErrorStatus,pensum,activeTerm,tab,saved,composing,myRating,ratingPending,ratingError,careerComments,allSubjects}=$derived(page);const{prerequisiteName,addComment,toggleSaved,rate,targetCommentId}=page;let savedDialog=$state<ConfirmDialog>();function handleSavedClick(){saved?savedDialog?.open():toggleSaved()}$effect(()=>{if(!loading&&tab==="community"&&targetCommentId){requestAnimationFrame(()=>document.getElementById(`comment-${targetCommentId}`)?.scrollIntoView({behavior:"smooth",block:"center"}))}})
</script>

<svelte:head><title>{career.name || "Carrera"} — Pathora</title></svelte:head>

<main>
	{#if loading}<PageLoader />
	{:else if loadError}<div class="page-shell"><PageState kind={loadErrorStatus === 404 ? "not-found" : "error"} title={loadErrorStatus === 404 ? "No encontramos esta carrera." : "No pudimos abrir la carrera."} message={loadError} /></div>
	{:else}
	<section class="detail-hero">
		{#if career.image}<img src={career.image} alt="" />{/if}
		<div class="shade"></div>
		<button class="back" onclick={() => history.back()}>← Volver</button>
		<div class="hero-content page-shell">
			<span>{career.school} · {career.code}</span>
			<h1>{career.name}</h1>
			<p>{career.heroSummary}</p>
			<div class="hero-actions">
				<button class="primary" onclick={() => (page.tab = "pensum")}
					>Ver pensum <Icon name="arrow" size={17} /></button
				>{#if $currentUser}<button class:saved onclick={handleSavedClick}
						><Icon name={saved ? "bookmark" : "heart"} size={18} />{saved
							? "Guardada"
							: "Guardar"}</button
					>{/if}
			</div>
		</div>
	</section>
	<div class="tabs-wrap">
		<nav class="page-shell">
			{#each [["overview", "Vista general"], ["pensum", "Pensum"], ["community", "Comunidad"]] as item}<button
					class:active={tab === item[0]}
					onclick={() => (page.tab = item[0] as CareerTab)}>{item[1]}</button
				>{/each}
		</nav>
	</div>
	{#if tab === "overview"}<section class="page-shell overview">
			<div class="main-copy">
				<span class="kicker">Una carrera para construir</span>
				<h2>Conoce {career.name}.</h2>
				<RichContent content={career.overview} />
				<div class="professional-profile">
					<h3>Tu futuro profesional</h3>
					<p>{career.professionalProfile}</p>
				</div>
				<div class="outcomes">
					<h3>Al terminar podrás</h3>
					{#each career.outcomes as outcome}<div>
							<span><Icon name="check" size={15} /></span>{outcome}
						</div>{/each}
				</div>
			</div>
			<aside>
				<div class="facts">
					<div>
						<span>Duración</span><strong>{career.terms} cuatrimestres</strong>
					</div>
					<div><span>Modalidad</span><strong>{career.mode}</strong></div>
					<div>
						<span>Valoración</span><strong
							>★ {career.rating} <small>({career.reviews})</small></strong
						>
					</div>
					<div><span>Créditos estimados</span><strong>144</strong></div>
				</div>
				<div class="accent-card" style={`--accent:${career.accent}`}>
					<span>Próximo paso</span>
					<h3>¿Quieres conocer la experiencia real?</h3>
					<button onclick={() => (page.tab = "community")}
						>Leer la comunidad <Icon name="arrow" size={16} /></button
					>
				</div>
			</aside>
		</section>
	{:else if tab === "pensum"}<section class="page-shell pensum">
			<header>
				<span class="kicker">Plan {apiPensum?.version}</span>
				<h2>Un camino, paso a paso.</h2>
				<p>{markdownSummary(apiPensum?.description ?? "")}</p>
			</header>
			<div class="term-nav">
				{#each pensum as _, i}<button
						class:active={activeTerm === i}
						onclick={() => (page.activeTerm = i)}
						>{String(i + 1).padStart(2, "0")}</button
					>{/each}
			</div>
			<div class="term">
				<div class="term-number">
					<span>Cuatrimestre</span><strong
						>{String(activeTerm + 1).padStart(2, "0")}</strong
					><small
						>{pensum[activeTerm]?.reduce((a, s) => a + s.credits, 0) ?? 0} créditos</small
					>
				</div>
				<div class="subjects">
					{#each pensum[activeTerm] ?? [] as subject, i}<article>
							<span>{subject.code}</span>
							<div>
								<h3>{subject.name}</h3>
								<p>
									{subject.credits} créditos · {subject.credits + 1} horas semanales
								</p>
								<div class="prerequisites">
									<span>Prerrequisitos</span>
									{#if subject.prerequisiteCodes.length}
										<div>
											{#each subject.prerequisiteCodes as code}<button
													title={prerequisiteName(code)}
													onclick={() => {
														const prerequisite = allSubjects.find(
															(item) => item.code === code,
														)
														if (prerequisite)
															activeTerm = pensum.findIndex((term) =>
																term.includes(prerequisite),
															)
													}}>{code} · {prerequisiteName(code)}</button
												>{/each}
										</div>
									{:else}<small>Sin prerrequisitos</small>{/if}
								</div>
							</div>
							<small>0{i + 1}</small>
						</article>{/each}
				</div>
			</div>
		</section>
	{:else}<section class="page-shell community">
			<header>
				<div>
					<span class="kicker">Voces reales</span>
					<h2>La experiencia,<br />contada por dentro.</h2>
				</div>
				<div class="rating">
					<strong>{rating?.average ?? career.rating}</strong><span>★★★★★</span
					><small>{rating?.count ?? career.reviews} valoraciones</small>
				</div>
			</header>
			{#if $currentUser}<div class="rate-feedback">
				<div class="rate-career" aria-busy={ratingPending !== null}>
					<span class="rate-label">Valora esta carrera</span
					>{#each [1, 2, 3, 4, 5] as value}<button
							class:active={myRating >= value}
							class:pending={ratingPending === value}
							disabled={ratingPending !== null}
							onclick={() => rate(value)}
							aria-label={`${value} estrellas`}
							>{#if ratingPending === value}<span
									class="rating-spinner"
									aria-hidden="true"></span
								>{:else}★{/if}</button
						>{/each}
				</div>
				{#if ratingPending !== null}<p class="rating-status" role="status"
						>Guardando tu valoración…</p
					>{:else if ratingError}<p class="rating-error" role="alert"
						>{ratingError}</p
					>{/if}
			</div>
				<button class="comment-cta" onclick={() => (composing = !composing)}
					>Comparte tu experiencia <Icon name="arrow" size={17} /></button
				>{/if}
			{#if composing}<div class="main-editor">
					<RichTextEditor
						placeholder="¿Cómo ha sido tu experiencia con esta carrera?"
						onSubmit={addComment}
					/>
				</div>{/if}
			<div class="comments">
				{#each careerComments as comment}<CommentThread
						{comment}
						useful={comment.useful}
						notUseful={comment.notUseful}
						currentVote={comment.currentVote}
						highlighted={targetCommentId === comment.id}
						repliesData={comment.repliesData}
					/>{/each}
			</div>
		</section>{/if}
	{/if}
</main>

<ConfirmDialog
	bind:this={savedDialog}
	title="¿Quitar carrera guardada?"
	description={`${career.name} dejará de aparecer en tu lista de carreras guardadas.`}
	confirmLabel="Sí, quitar de guardadas"
	pendingLabel="Quitando…"
	icon="bookmark"
	onConfirm={toggleSaved}
/>

<style>
	.detail-hero {
		height: min(680px, calc(100vh - 82px));
		min-height: 590px;
		position: relative;
		color: white;
	}
	.detail-hero > img,
	.shade {
		position: absolute;
		inset: 0;
		width: 100%;
		height: 100%;
		object-fit: cover;
		object-position: center;
	}
	.shade {
		background: linear-gradient(
				90deg,
				rgba(10, 10, 8, 0.88),
				rgba(10, 10, 8, 0.18) 62%,
				rgba(10, 10, 8, 0.04)
			),
			linear-gradient(0deg, rgba(10, 10, 8, 0.45), transparent 50%);
	}
	.back {
		position: absolute;
		z-index: 2;
		top: 2rem;
		left: max(1.5rem, calc((100vw - 1280px) / 2));
		border: 0;
		border-radius: 999px;
		padding: 0.7rem 1rem;
		background: rgba(255, 255, 255, 0.15);
		backdrop-filter: blur(14px);
		color: white;
		font: inherit;
		cursor: pointer;
	}
	.hero-content {
		position: relative;
		z-index: 1;
		height: 100%;
		display: flex;
		flex-direction: column;
		justify-content: end;
		padding-bottom: 3.5rem;
	}
	.hero-content > span,
	.kicker {
		text-transform: uppercase;
		letter-spacing: 0.14em;
		font-size: 0.68rem;
		font-weight: 700;
	}
	.hero-content h1 {
		font-family: var(--display);
		font-size: clamp(3.8rem, 6.8vw, 7.4rem);
		line-height: 0.88;
		letter-spacing: -0.07em;
		max-width: 860px;
		margin: 1.2rem 0 1.7rem;
	}
	.hero-content p {
		max-width: 580px;
		font-size: 1.05rem;
		line-height: 1.6;
		color: rgba(255, 255, 255, 0.72);
	}
	.hero-actions {
		display: flex;
		gap: 0.6rem;
		margin-top: 1.5rem;
	}
	.hero-actions button,
	.accent-card button {
		border: 0;
		border-radius: 12px;
		padding: 0.85rem 1rem;
		display: flex;
		align-items: center;
		gap: 0.5rem;
		font: inherit;
		font-size: 0.82rem;
		font-weight: 650;
		cursor: pointer;
		background: rgba(255, 255, 255, 0.16);
		color: white;
		backdrop-filter: blur(15px);
	}
	.hero-actions .primary {
		background: white;
		color: var(--ink);
	}
	.hero-actions .saved {
		background: var(--accent);
		color: var(--ink);
	}
	.tabs-wrap {
		position: sticky;
		top: 82px;
		z-index: 20;
		background: rgba(250, 249, 246, 0.86);
		backdrop-filter: blur(20px);
		border-bottom: 1px solid var(--line);
	}
	.tabs-wrap nav {
		display: flex;
		gap: 2rem;
	}
	.tabs-wrap button {
		padding: 1.25rem 0;
		border: 0;
		border-bottom: 2px solid transparent;
		background: none;
		font: inherit;
		font-size: 0.8rem;
		color: var(--muted);
		cursor: pointer;
	}
	.tabs-wrap button.active {
		color: var(--ink);
		border-color: var(--ink);
	}
	.overview {
		display: grid;
		grid-template-columns: 1fr 370px;
		gap: 9vw;
		padding-top: 8rem;
		padding-bottom: 9rem;
	}
	.kicker {
		color: var(--muted);
	}
	.overview h2,
	.pensum h2,
	.community h2 {
		font-family: var(--display);
		font-size: clamp(3rem, 6vw, 5.7rem);
		line-height: 0.92;
		letter-spacing: -0.06em;
		margin: 1.4rem 0 2rem;
	}
	.pensum header p {
		max-width: 650px;
		font-size: 1.08rem;
		line-height: 1.8;
		color: var(--muted);
	}
	.outcomes {
		margin-top: 4rem;
	}
	.professional-profile {
		margin-top: 2.5rem;
		padding-top: 2rem;
		border-top: 1px solid var(--line);
	}
	.professional-profile h3 {
		font-size: 0.78rem;
		letter-spacing: 0.02em;
	}
	.professional-profile p {
		color: var(--muted);
		font-size: 1rem;
		line-height: 1.75;
	}
	.outcomes h3 {
		font-family: var(--display);
		font-size: 1.5rem;
	}
	.outcomes div {
		padding: 1rem 0;
		border-bottom: 1px solid var(--line);
		display: flex;
		align-items: center;
		gap: 0.75rem;
	}
	.outcomes div span {
		width: 28px;
		height: 28px;
		border-radius: 50%;
		display: grid;
		place-items: center;
		background: var(--accent);
	}
	aside {
		display: flex;
		flex-direction: column;
		gap: 1rem;
	}
	.facts {
		padding: 1rem 1.5rem;
		border-radius: 22px;
		background: var(--surface);
		border: 1px solid var(--line);
	}
	.facts div {
		display: flex;
		justify-content: space-between;
		gap: 1rem;
		padding: 1.2rem 0;
		border-bottom: 1px solid var(--line);
	}
	.facts div:last-child {
		border: 0;
	}
	.facts span {
		font-size: 0.75rem;
		color: var(--muted);
	}
	.facts strong {
		font-size: 0.85rem;
	}
	.facts small {
		color: var(--muted);
	}
	.accent-card {
		padding: 1.6rem;
		border-radius: 22px;
		background: var(--accent);
	}
	.accent-card > span {
		font-size: 0.68rem;
		text-transform: uppercase;
		letter-spacing: 0.1em;
	}
	.accent-card h3 {
		font-family: var(--display);
		font-size: 1.65rem;
		line-height: 1.1;
	}
	.accent-card button {
		background: var(--ink);
	}
	.pensum,
	.community {
		padding-top: 7rem;
		padding-bottom: 9rem;
	}
	.pensum header {
		max-width: 800px;
	}
	.term-nav {
		display: flex;
		gap: 0.35rem;
		margin: 4rem 0 2rem;
		overflow: auto;
		padding-bottom: 0.5rem;
	}
	.term-nav button {
		flex: 0 0 50px;
		height: 50px;
		border: 1px solid var(--line);
		border-radius: 12px;
		background: var(--surface);
		font: inherit;
		font-size: 0.75rem;
		cursor: pointer;
	}
	.term-nav button.active {
		background: var(--ink);
		color: white;
	}
	.term {
		display: grid;
		grid-template-columns: 260px 1fr;
		gap: 4rem;
		padding: 3rem;
		border-radius: 26px;
		background: var(--surface);
		border: 1px solid var(--line);
	}
	.term-number {
		display: flex;
		flex-direction: column;
	}
	.term-number span,
	.term-number small {
		font-size: 0.7rem;
		text-transform: uppercase;
		letter-spacing: 0.1em;
		color: var(--muted);
	}
	.term-number strong {
		font-family: var(--display);
		font-size: 7rem;
		line-height: 1;
		margin: 1rem 0;
	}
	.subjects article {
		display: grid;
		grid-template-columns: 90px 1fr 30px;
		gap: 1rem;
		padding: 1.2rem 0;
		border-bottom: 1px solid var(--line);
		align-items: center;
	}
	.subjects article > span,
	.subjects article > small {
		font-size: 0.65rem;
		color: var(--muted);
	}
	.subjects h3 {
		font-size: 0.95rem;
		margin: 0 0 0.35rem;
	}
	.subjects p {
		font-size: 0.75rem;
		color: var(--muted);
		margin: 0;
	}
	.prerequisites {
		margin-top: 0.75rem;
		display: flex;
		align-items: flex-start;
		gap: 0.75rem;
		flex-wrap: wrap;
	}
	.prerequisites > span {
		padding-top: 0.28rem;
		font-size: 0.6rem;
		text-transform: uppercase;
		letter-spacing: 0.09em;
		color: var(--muted);
	}
	.prerequisites > div {
		display: flex;
		gap: 0.35rem;
		flex-wrap: wrap;
	}
	.prerequisites button {
		border: 1px solid var(--line);
		border-radius: 7px;
		background: var(--surface-2);
		padding: 0.28rem 0.45rem;
		font: inherit;
		font-size: 0.6rem;
		color: #555;
		cursor: pointer;
	}
	.prerequisites button:hover {
		border-color: #a3c548;
		background: rgba(202, 255, 79, 0.14);
	}
	.prerequisites small {
		padding-top: 0.25rem;
		font-size: 0.62rem;
		color: #999;
	}
	.community header {
		display: flex;
		justify-content: space-between;
		align-items: end;
	}
	.rating {
		display: flex;
		flex-direction: column;
		align-items: end;
	}
	.rating strong {
		font-family: var(--display);
		font-size: 5rem;
		line-height: 1;
	}
	.rating span {
		color: #e1a600;
		letter-spacing: 0.1em;
	}
	.rating small {
		color: var(--muted);
		margin-top: 0.4rem;
	}
	.comment-cta {
		margin: 3rem 0 1rem;
		border: 0;
		border-radius: 12px;
		padding: 0.9rem 1rem;
		background: var(--ink);
		color: white;
		display: flex;
		align-items: center;
		gap: 0.5rem;
		font: inherit;
		cursor: pointer;
	}
	.rate-career {
		display: flex;
		align-items: center;
		gap: 0.25rem;
		margin-top: 2rem;
	}
	.rate-career .rate-label {
		margin-right: 0.6rem;
		font-size: 0.72rem;
		color: var(--muted);
	}
	.rate-career button {
		border: 0;
		background: none;
		padding: 0.15rem;
		color: #ccc;
		font-size: 1.35rem;
		cursor: pointer;
		transition:
			color 160ms ease-out,
			transform 160ms ease-out;
	}
	.rate-career button.active,
	.rate-career button:hover {
		color: #dca400;
	}
	.rate-career button:not(:disabled):active {
		transform: scale(0.9);
	}
	.rate-career button:disabled {
		cursor: wait;
	}
	.rate-career button.pending {
		width: 1.65rem;
		display: grid;
		place-items: center;
	}
	.rating-spinner {
		width: 15px;
		height: 15px;
		border: 2px solid rgba(220, 164, 0, 0.28);
		border-top-color: #dca400;
		border-radius: 50%;
		animation: rating-spin 650ms linear infinite;
	}
	.rating-status,
	.rating-error {
		margin: 0.35rem 0 1rem;
		font-size: 0.72rem;
	}
	.rating-status {
		color: var(--muted);
	}
	.rating-error {
		color: #9d2d24;
	}
	@keyframes rating-spin {
		to {
			transform: rotate(360deg);
		}
	}
	.main-editor {
		max-width: 900px;
		margin: 0 0 1rem;
	}
	.comments {
		max-width: 900px;
	}
	@media (max-width: 850px) {
		.overview {
			grid-template-columns: 1fr;
		}
		.term {
			grid-template-columns: 1fr;
			gap: 1rem;
		}
		.term-number strong {
			font-size: 4rem;
		}
		.community header {
			align-items: start;
			flex-direction: column;
		}
		.rating {
			align-items: start;
		}
	}
	@media (prefers-reduced-motion: reduce) {
		.rate-career button {
			transition: none;
		}
		.rating-spinner {
			animation: none;
		}
	}
	@media (max-width: 600px) {
		.detail-hero {
			height: 620px;
		}
		.hero-content h1 {
			font-size: 4rem;
		}
		.tabs-wrap {
			top: 78px;
		}
		.overview,
		.pensum,
		.community {
			padding-top: 5rem;
		}
		.term {
			padding: 1.3rem;
		}
		.subjects article {
			grid-template-columns: 60px 1fr;
		}
		.subjects article > small {
			display: none;
		}
		.tabs-wrap nav {
			gap: 1.2rem;
		}
	}
</style>
