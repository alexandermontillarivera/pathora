<script lang="ts">
	import RichTextEditor from "@components/form/rich-text-editor.svelte"
	import CommentContent from "@components/comments/comment-content.svelte"
	import ConfirmDialog from "@components/ui/confirm-dialog.svelte"
	import Icon from "@components/ui/icon.svelte"
	import { navigate } from "@lib/router"
	import { link } from "@lib/actions/link"
	import { commentService } from "@lib/services/comment-service"
	import type { ApiComment } from '@lib/api/api-types'
	import { currentUser } from '@stores/auth'
	import UserAvatar from "@components/profile/user-avatar.svelte"
	type Vote = "useful" | "not-useful" | null
	type Reply = {
		id: number
		userId: number
		name: string
		initials: string
		avatarSeed?: string
		time: string
		html: string
		useful: number
		notUseful: number
		vote: Vote
		voting: Vote
	}
	let {
		comment,
		career,
		careerId,
		highlighted = false,
		useful = 0,
		notUseful = 0,
		currentVote = null,
		repliesData = [],
		onDeleted,
	}: {
		comment: {
			id: number
			userId?: number
			name: string
			initials: string
			avatarSeed?: string
			time: string
			content?: unknown
			text?: string
			rating: number
		}
		career?: string
		careerId?: number
		highlighted?: boolean
		useful?: number
		notUseful?: number
		currentVote?: boolean | null
		repliesData?: ApiComment[]
		onDeleted?: (id: number) => void
	} = $props()
	let deleteDialog = $state<ConfirmDialog>()
	let deleteTarget = $state<{ id: number; reply: boolean }>()
	let removed = $state(false)
	let replyOpen = $state(false)
	let vote = $state<Vote>(null)
	let voting = $state<Vote>(null)
	let voteError = $state("")
	let usefulCount = $state(0)
	let notUsefulCount = $state(0)
	let replies = $state<Reply[]>([])
	let initialized = false
	$effect(() => {
		if (!initialized) {
			initialized = true
			usefulCount = useful
			notUsefulCount = notUseful
			vote =
				currentVote === null ? null : currentVote ? "useful" : "not-useful"
			replies = repliesData.map(item => ({
				id:item.id,userId:item.author.id,name:`${item.author.firstName} ${item.author.lastName}`,
				initials:`${item.author.firstName[0]??''}${item.author.lastName[0]??''}`,
				avatarSeed:item.author.avatarSeed,
				time:new Intl.DateTimeFormat('es',{dateStyle:'medium'}).format(new Date(item.createdAt)),
				html:item.content&&typeof item.content==='object'&&'html' in item.content?String((item.content as {html:string}).html):String(item.content),
				useful:item.useful,notUseful:item.notUseful,
				vote:item.currentVote===null?null:item.currentVote?'useful':'not-useful',voting:null,
			}))
		}
	})
	async function addReply(html: string) {
		const created = await commentService.reply(comment.id, html)
		replies = [
			...replies,
			{
				id: created.id,
				userId: created.author.id,
				name: `${created.author.firstName} ${created.author.lastName}`,
				initials: `${created.author.firstName[0]}${created.author.lastName[0]}`,
				avatarSeed: created.author.avatarSeed,
				time: "Ahora",
				html,
				useful: 0,
				notUseful: 0,
				vote: null,
				voting: null,
			},
		]
		replyOpen = false
	}
	async function castVote(next: Exclude<Vote, null>) {
		if (voting || vote === next) return
		voting = next
		voteError = ""
		try {
			const result = await commentService.vote(comment.id, next === "useful")
			usefulCount = result.useful
			notUsefulCount = result.notUseful
			vote =
				result.currentVote === null
					? null
					: result.currentVote
						? "useful"
						: "not-useful"
		} catch {
			voteError = "No pudimos guardar tu opinión. Inténtalo otra vez."
		} finally {
			voting = null
		}
	}
	async function voteReply(id: number, next: Exclude<Vote, null>) {
		const target = replies.find((reply) => reply.id === id)
		if (!target || target.voting || target.vote === next) return
		voteError = ""
		replies = replies.map((reply) =>
			reply.id === id ? { ...reply, voting: next } : reply,
		)
		try {
			const result = await commentService.vote(id, next === "useful")
			const nextVote: Vote =
				result.currentVote === null
					? null
					: result.currentVote
						? "useful"
						: "not-useful"
			replies = replies.map((reply) =>
				reply.id === id
					? {
							...reply,
							useful: result.useful,
							notUseful: result.notUseful,
							vote: nextVote,
							voting: null,
						}
					: reply,
			)
		} catch {
			replies = replies.map((reply) =>
				reply.id === id ? { ...reply, voting: null } : reply,
			)
			voteError = "No pudimos guardar tu opinión. Inténtalo otra vez."
		}
	}
	function user(id?: number) {
		if (id) navigate(`/users/${id}`)
	}
	function askToDelete(id: number, reply = false) {
		deleteTarget = { id, reply }
		deleteDialog?.open()
	}
	async function deleteSelected() {
		if (!deleteTarget) return
		await commentService.remove(deleteTarget.id)
		if (deleteTarget.reply) {
			replies = replies.filter((reply) => reply.id !== deleteTarget?.id)
		} else {
			removed = true
			onDeleted?.(deleteTarget.id)
		}
	}
</script>

{#if !removed}
<article class="thread" class:highlighted id={`comment-${comment.id}`}>
	<button
		class="avatar"
		onclick={() => user(comment.userId)}
		aria-label={`Ver perfil de ${comment.name}`}><UserAvatar
			seed={comment.avatarSeed}
			initials={comment.initials}
			name={comment.name}
			size={50}
		/></button
	>
	<div class="content">
		<div class="meta">
			<button onclick={() => user(comment.userId)}>{comment.name}</button
			>{#if career && careerId}<a
				href={`/careers/${careerId}?tab=community&comment=${comment.id}`}
				use:link={{
					path: `/careers/${careerId}`,
					query: { tab: "community", comment: String(comment.id) },
					stripQuery: true,
				}}
				>sobre {career}</a
			>{:else if career}<span>sobre {career}</span>{/if}<small>{comment.time}</small>
		</div>
		<div class="stars">
			{"★".repeat(comment.rating)}{"☆".repeat(5 - comment.rating)}
		</div>
		<CommentContent content={comment.content ?? comment.text ?? ""} />
		{#if $currentUser}<div class="actions" aria-busy={voting !== null}>
			<span>¿Te pareció útil?</span><button
				class:active={vote === "useful"}
				class:loading={voting === "useful"}
				disabled={voting !== null || vote === "useful"}
				onclick={() => castVote("useful")}
				aria-pressed={vote === "useful"}
				>{#if voting === "useful"}<span class="spinner" aria-hidden="true"></span>
				{:else}<Icon name="thumbUp" size={14} />{/if}Sí · {usefulCount}</button
			><button
				class:active={vote === "not-useful"}
				class:loading={voting === "not-useful"}
				disabled={voting !== null || vote === "not-useful"}
				onclick={() => castVote("not-useful")}
				aria-pressed={vote === "not-useful"}
				>{#if voting === "not-useful"}<span class="spinner" aria-hidden="true"></span>
				{:else}<Icon name="thumbDown" size={14} />{/if}No · {notUsefulCount}</button
			><button class="respond" onclick={() => (replyOpen = !replyOpen)}
				><Icon name="message" size={14} />Responder</button
			>
			{#if $currentUser?.id === comment.userId}<button
				class="delete-action"
				onclick={() => askToDelete(comment.id)}
				aria-label="Eliminar comentario"
				><Icon name="trash" size={14} />Eliminar</button
			>{/if}
		</div>{#if voteError}<p class="vote-error" role="alert">{voteError}</p>{/if}{/if}
		{#if $currentUser&&replyOpen}<div class="reply-editor">
				<RichTextEditor
					compact
					placeholder={`Responder a ${comment.name}…`}
					onSubmit={addReply}
				/>
			</div>{/if}{#if replies.length}<div class="replies">
				{#each replies as reply}
					<div class="reply">
						<button class="reply-avatar" onclick={() => user(reply.userId)}
							aria-label={`Ver perfil de ${reply.name}`}><UserAvatar
								seed={reply.avatarSeed}
								initials={reply.initials}
								name={reply.name}
								size={36}
							/></button
						>
						<div>
							<div class="reply-meta">
								<button onclick={() => user(reply.userId)}>{reply.name}</button
								><span>{reply.time}</span>
							</div>
							<div class="reply-content"><CommentContent content={reply.html} compact /></div>
							{#if $currentUser}<div class="reply-actions">
								<button
									class:active={reply.vote === "useful"}
									class:loading={reply.voting === "useful"}
									disabled={reply.voting !== null || reply.vote === "useful"}
									onclick={() => voteReply(reply.id, "useful")}
									>{#if reply.voting === "useful"}<span class="spinner" aria-hidden="true"></span>
									{:else}<Icon name="thumbUp" size={13} />{/if}{reply.useful}</button
								><button
									class:active={reply.vote === "not-useful"}
									class:loading={reply.voting === "not-useful"}
									disabled={reply.voting !== null || reply.vote === "not-useful"}
									onclick={() => voteReply(reply.id, "not-useful")}
									>{#if reply.voting === "not-useful"}<span class="spinner" aria-hidden="true"></span>
									{:else}<Icon name="thumbDown" size={13} />{/if}{reply.notUseful}</button
								><button onclick={() => (replyOpen = true)}>Responder</button>
								{#if $currentUser?.id === reply.userId}<button
									class="delete-action"
									onclick={() => askToDelete(reply.id, true)}
									aria-label="Eliminar respuesta"
									><Icon name="trash" size={13} />Eliminar</button
								>{/if}
							</div>{/if}
						</div>
					</div>{/each}
			</div>{/if}
	</div>
</article>
{/if}

<ConfirmDialog
	bind:this={deleteDialog}
	title={deleteTarget?.reply ? "¿Eliminar respuesta?" : "¿Eliminar comentario?"}
	description={deleteTarget?.reply
		? "Esta respuesta desaparecerá de la conversación y no podrás recuperarla."
		: "El comentario y sus respuestas desaparecerán permanentemente."}
	confirmLabel={deleteTarget?.reply ? "Sí, eliminar respuesta" : "Sí, eliminar comentario"}
	onConfirm={deleteSelected}
/>

<style>
	.thread {
		display: grid;
		grid-template-columns: 50px 1fr;
		gap: 1.2rem;
		padding: 2rem 0;
		border-bottom: 1px solid var(--line);
		border-radius: 12px;
		transition: background-color 600ms ease-out, box-shadow 600ms ease-out;
	}
	.thread.highlighted {
		animation: comment-highlight 3.5s ease-out forwards;
	}
	@keyframes comment-highlight {
		0%, 45% {
			background: rgba(202, 255, 79, 0.18);
			box-shadow: 0 0 0 8px rgba(202, 255, 79, 0.18);
		}
		100% {
			background: transparent;
			box-shadow: 0 0 0 8px transparent;
		}
	}
	button {
		font: inherit;
		cursor: pointer;
	}
	.avatar,
	.reply-avatar {
		border: 0;
		padding: 0;
		display: grid;
		place-items: center;
		background: transparent;
	}
	.avatar {
		width: 50px;
		height: 50px;
		border-radius: 16px;
	}
	.meta {
		display: flex;
		align-items: center;
		gap: 0.5rem;
		flex-wrap: wrap;
	}
	.meta button,
	.reply-meta button {
		border: 0;
		background: none;
		padding: 0;
		font-weight: 700;
	}
	.meta button:hover,
	.reply-meta button:hover {
		text-decoration: underline;
	}
	.meta span,
	.meta a,
	.meta small,
	.reply-meta span {
		font-size: 0.7rem;
		color: var(--muted);
	}
	.meta a {
		color: #567600;
		text-decoration: none;
		font-weight: 600;
	}
	.meta a:hover { text-decoration: underline; }
	.meta small {
		margin-left: auto;
	}
	.stars {
		color: #d59d00;
		margin: 0.55rem 0;
	}
	.actions,
	.reply-actions {
		display: flex;
		align-items: center;
		gap: 0.35rem;
		flex-wrap: wrap;
	}
	.actions > span {
		font-size: 0.65rem;
		color: var(--muted);
		margin-right: 0.25rem;
	}
	.actions button,
	.reply-actions button {
		border: 1px solid var(--line);
		border-radius: 999px;
		background: transparent;
		padding: 0.35rem 0.55rem;
		display: flex;
		align-items: center;
		gap: 0.3rem;
		font-size: 0.65rem;
		color: var(--muted);
		transition:
			background-color 180ms ease-out,
			border-color 180ms ease-out,
			color 180ms ease-out,
			transform 180ms ease-out;
	}
	.actions button:not(:disabled):active,
	.reply-actions button:not(:disabled):active {
		transform: scale(0.96);
	}
	.actions button:disabled,
	.reply-actions button:disabled {
		cursor: wait;
		opacity: 0.58;
	}
	.actions button.loading,
	.reply-actions button.loading {
		opacity: 1;
		color: var(--ink);
		background: var(--surface-2);
	}
	.actions button.active:disabled,
	.reply-actions button.active:disabled {
		cursor: default;
		opacity: 1;
	}
	.actions button.active,
	.reply-actions button.active {
		border-color: #9abd39;
		background: rgba(202, 255, 79, 0.18);
		color: var(--ink);
	}
	.actions .respond {
		border: 0;
		margin-left: 0.25rem;
	}
	.actions .delete-action,
	.reply-actions .delete-action {
		border: 0;
		color: #8d2c24;
	}
	.spinner {
		width: 13px;
		height: 13px;
		border: 1.5px solid currentColor;
		border-top-color: transparent;
		border-radius: 50%;
		animation: vote-spin 650ms linear infinite;
	}
	.vote-error {
		margin: 0.55rem 0 0;
		color: #9d2d24;
		font-size: 0.72rem;
	}
	@keyframes vote-spin {
		to {
			transform: rotate(360deg);
		}
	}
	.reply-editor {
		margin-top: 1rem;
	}
	.replies {
		margin-top: 1.2rem;
		padding-left: 1rem;
		border-left: 2px solid var(--surface-2);
	}
	.reply {
		display: grid;
		grid-template-columns: 36px 1fr;
		gap: 0.75rem;
		padding: 1rem 0;
	}
	.reply + .reply {
		border-top: 1px solid var(--line);
	}
	.reply-avatar {
		width: 36px;
		height: 36px;
		border-radius: 11px;
	}
	.reply-meta {
		display: flex;
		gap: 0.6rem;
		align-items: center;
	}
	.reply-content {
		margin: 0.35rem 0;
		font-size: 0.82rem;
		line-height: 1.6;
		color: #555;
	}
	@media (max-width: 600px) {
		.thread {
			grid-template-columns: 1fr;
		}
		.avatar {
			display: none;
		}
		.meta small {
			width: 100%;
		}
		.replies {
			padding-left: 0.6rem;
		}
		.actions > span {
			width: 100%;
		}
	}
	@media (prefers-reduced-motion: reduce) {
		.thread { transition: none; }
		.thread.highlighted {
			animation: none;
			background: rgba(202, 255, 79, 0.18);
		}
		.spinner {
			animation: none;
		}
		.actions button,
		.reply-actions button {
			transition: none;
		}
	}
</style>
