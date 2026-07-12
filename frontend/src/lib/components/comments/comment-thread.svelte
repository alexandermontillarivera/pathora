<script lang="ts">
	import RichTextEditor from "@components/form/rich-text-editor.svelte"
	import Icon from "@components/ui/icon.svelte"
	import { navigate } from "@lib/router"
	import { commentService } from "@lib/services/comment-service"
	import type { ApiComment } from '@lib/api/api-types'
	import { currentUser } from '@stores/auth'
	type Vote = "useful" | "not-useful" | null
	type Reply = {
		id: number
		userId: number
		name: string
		initials: string
		time: string
		html: string
		useful: number
		notUseful: number
		vote: Vote
	}
	let {
		comment,
		career,
		useful = 12,
		repliesData = [],
	}: {
		comment: {
			id: number
			userId?: number
			name: string
			initials: string
			time: string
			text: string
			rating: number
		}
		career?: string
		useful?: number
		repliesData?: ApiComment[]
	} = $props()
	let replyOpen = $state(false)
	let vote = $state<Vote>(null)
	let usefulCount = $state(0)
	let notUsefulCount = $state(1)
	let replies = $state<Reply[]>([])
	let initialized = false
	$effect(() => {
		if (!initialized) {
			initialized = true
			usefulCount = useful
			replies = repliesData.map(item => ({
				id:item.id,userId:item.author.id,name:`${item.author.firstName} ${item.author.lastName}`,
				initials:`${item.author.firstName[0]??''}${item.author.lastName[0]??''}`,
				time:new Intl.DateTimeFormat('es',{dateStyle:'medium'}).format(new Date(item.createdAt)),
				html:item.content&&typeof item.content==='object'&&'html' in item.content?String((item.content as {html:string}).html):String(item.content),
				useful:item.useful,notUseful:item.notUseful,vote:null,
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
				time: "Ahora",
				html,
				useful: 0,
				notUseful: 0,
				vote: null,
			},
		]
		replyOpen = false
	}
	async function castVote(next: Exclude<Vote, null>) {
		const result =
			vote === next
				? await commentService.removeVote(comment.id)
				: await commentService.vote(comment.id, next === "useful")
		usefulCount = result.useful
		notUsefulCount = result.notUseful
		vote =
			result.currentVote === null
				? null
				: result.currentVote
					? "useful"
					: "not-useful"
	}
	function voteReply(id: number, next: Exclude<Vote, null>) {
		replies = replies.map((reply) => {
			if (reply.id !== id) return reply
			let { useful, notUseful, vote } = reply
			if (vote === next) {
				next === "useful" ? useful-- : notUseful--
				return { ...reply, useful, notUseful, vote: null }
			}
			if (vote === "useful") useful--
			if (vote === "not-useful") notUseful--
			next === "useful" ? useful++ : notUseful++
			return { ...reply, useful, notUseful, vote: next }
		})
	}
	function user(id?: number) {
		if (id) navigate(`/users/${id}`)
	}
</script>

<article class="thread">
	<button
		class="avatar"
		onclick={() => user(comment.userId)}
		aria-label={`Ver perfil de ${comment.name}`}>{comment.initials}</button
	>
	<div class="content">
		<div class="meta">
			<button onclick={() => user(comment.userId)}>{comment.name}</button
			>{#if career}<span>sobre {career}</span>{/if}<small>{comment.time}</small>
		</div>
		<div class="stars">
			{"★".repeat(comment.rating)}{"☆".repeat(5 - comment.rating)}
		</div>
		<p>{comment.text}</p>
		{#if $currentUser}<div class="actions">
			<span>¿Te pareció útil?</span><button
				class:active={vote === "useful"}
				onclick={() => castVote("useful")}
				aria-pressed={vote === "useful"}
				><Icon name="thumbUp" size={14} />Sí · {usefulCount}</button
			><button
				class:active={vote === "not-useful"}
				onclick={() => castVote("not-useful")}
				aria-pressed={vote === "not-useful"}
				><Icon name="thumbDown" size={14} />No · {notUsefulCount}</button
			><button class="respond" onclick={() => (replyOpen = !replyOpen)}
				><Icon name="message" size={14} />Responder</button
			>
		</div>{/if}
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
							>{reply.initials}</button
						>
						<div>
							<div class="reply-meta">
								<button onclick={() => user(reply.userId)}>{reply.name}</button
								><span>{reply.time}</span>
							</div>
							<div class="reply-content">{@html reply.html}</div>
							{#if $currentUser}<div class="reply-actions">
								<button
									class:active={reply.vote === "useful"}
									onclick={() => voteReply(reply.id, "useful")}
									><Icon name="thumbUp" size={13} />{reply.useful}</button
								><button
									class:active={reply.vote === "not-useful"}
									onclick={() => voteReply(reply.id, "not-useful")}
									><Icon name="thumbDown" size={13} />{reply.notUseful}</button
								><button onclick={() => (replyOpen = true)}>Responder</button>
							</div>{/if}
						</div>
					</div>{/each}
			</div>{/if}
	</div>
</article>

<style>
	.thread {
		display: grid;
		grid-template-columns: 50px 1fr;
		gap: 1.2rem;
		padding: 2rem 0;
		border-bottom: 1px solid var(--line);
	}
	button {
		font: inherit;
		cursor: pointer;
	}
	.avatar,
	.reply-avatar {
		border: 0;
		display: grid;
		place-items: center;
		background: var(--surface-2);
		font-size: 0.72rem;
		font-weight: 700;
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
	.meta small,
	.reply-meta span {
		font-size: 0.7rem;
		color: var(--muted);
	}
	.meta small {
		margin-left: auto;
	}
	.stars {
		color: #d59d00;
		margin: 0.55rem 0;
	}
	.content > p {
		font-size: 1rem;
		line-height: 1.7;
		color: #444;
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
	.reply-content :global(p) {
		margin: 0.2rem 0;
	}
	.reply-content :global(blockquote) {
		margin: 0.5rem 0;
		padding-left: 0.8rem;
		border-left: 3px solid var(--accent);
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
</style>
