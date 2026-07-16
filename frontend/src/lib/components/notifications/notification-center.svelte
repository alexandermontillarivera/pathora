<script lang="ts">
	import { notificationService } from "@lib/services/notification-service"
	import type { ApiNotification } from "@lib/api/api-types"
	import { link } from "@lib/actions/link"
	import Icon from "@components/ui/icon.svelte"
	let root: HTMLDivElement
	let open = $state(false)
	let notifications = $state<ApiNotification[]>([])
	let unread = $state(0)
	let loading = $state(false)
	async function load() {
		loading = true
		try {
			const [page, summary] = await Promise.all([
				notificationService.list(1, 5),
				notificationService.summary(),
			])
			notifications = page.records
			unread = summary.unread
		} catch {
			notifications = []
			unread = 0
		} finally {
			loading = false
		}
	}
	async function toggle() {
		open = !open
		if (open) await load()
	}
	async function read(item: ApiNotification) {
		if (!item.read) {
			await notificationService.read(item.id)
			item.read = true
			unread = Math.max(0, unread - 1)
		}
	}
	async function markAll() {
		await notificationService.readAll()
		notifications = notifications.map((item) => ({ ...item, read: true }))
		unread = 0
	}
	$effect(() => {
		if (!open) return
		const outside = (event: PointerEvent) => {
			if (!root.contains(event.target as Node)) open = false
		}
		const keyboard = (event: KeyboardEvent) => {
			if (event.key === "Escape") open = false
		}
		document.addEventListener("pointerdown", outside)
		document.addEventListener("keydown", keyboard)
		return () => {
			document.removeEventListener("pointerdown", outside)
			document.removeEventListener("keydown", keyboard)
		}
	})
</script>

<div class="notifications" bind:this={root}>
	<button
		class="trigger"
		class:active={open}
		onclick={toggle}
		aria-label="Notificaciones"
		aria-expanded={open}
		><Icon name="bell" size={19} />{#if unread}<span>{unread}</span
			>{/if}</button
	>{#if open}<div class="popover">
			<header>
				<div>
					<span>Tu actividad</span>
					<h3>Notificaciones</h3>
				</div>
				<button onclick={markAll}>Marcar leídas</button>
			</header>
			<div class="list">
				{#if loading}<p class="state">
						Cargando…
					</p>{:else if notifications.length}{#each notifications as item}<a
							class:unread={!item.read}
							href={item.targetUrl ?? "/activity"}
							use:link
							onclick={() => read(item)}
							><i
								><Icon
									name={item.type === "REPLY" ? "message" : "user"}
									size={17}
								/></i
							>
							<div>
								<strong>{item.title}</strong>
								<p>{item.detail}</p>
								<small
									>{new Intl.DateTimeFormat("es", {
										dateStyle: "medium",
									}).format(new Date(item.createdAt))}</small
								>
							</div></a
						>{/each}{:else}<p class="state">
						No tienes notificaciones nuevas.
					</p>{/if}
			</div>
			<footer>
				<a href="/activity" use:link onclick={() => (open = false)}
					>Ver toda la actividad <Icon name="arrow" size={15} /></a
				>
			</footer>
		</div>{/if}
</div>

<style>
	.notifications {
		position: relative;
	}
	.trigger {
		position: relative;
		width: 38px;
		height: 38px;
		border: 0;
		border-radius: 12px;
		background: var(--surface-2);
		display: grid;
		place-items: center;
		cursor: pointer;
	}
	.trigger.active {
		background: var(--ink);
		color: white;
	}
	.trigger > span {
		position: absolute;
		right: -4px;
		top: -4px;
		min-width: 17px;
		height: 17px;
		border: 2px solid #faf9f6;
		border-radius: 99px;
		background: var(--accent);
		font-size: 0.58rem;
		display: grid;
		place-items: center;
	}
	.popover {
		position: absolute;
		right: 0;
		top: 48px;
		width: min(390px, calc(100vw - 2rem));
		border: 1px solid var(--line);
		border-radius: 20px;
		background: rgba(253, 252, 249, 0.98);
		box-shadow: 0 24px 70px rgba(25, 25, 20, 0.18);
		overflow: hidden;
	}
	.popover header {
		padding: 1.2rem;
		display: flex;
		justify-content: space-between;
	}
	.popover header span {
		font-size: 0.62rem;
		text-transform: uppercase;
		color: var(--muted);
	}
	h3 {
		font-family: var(--display);
		font-size: 1.6rem;
		margin: 0.25rem 0;
	}
	.popover header button {
		border: 0;
		background: none;
		color: var(--muted);
		font: inherit;
		font-size: 0.65rem;
	}
	.list {
		border-block: 1px solid var(--line);
	}
	.list > a {
		padding: 1rem 1.2rem;
		border-bottom: 1px solid var(--line);
		display: grid;
		grid-template-columns: 38px 1fr;
		gap: 0.8rem;
		color: inherit;
		text-decoration: none;
	}
	.list > a.unread {
		background: rgba(202, 255, 79, 0.09);
	}
	.list i {
		width: 38px;
		height: 38px;
		border-radius: 11px;
		background: var(--surface-2);
		display: grid;
		place-items: center;
	}
	.list strong {
		font-size: 0.76rem;
	}
	.list p {
		margin: 0.25rem 0;
		font-size: 0.68rem;
		color: var(--muted);
	}
	.list small {
		font-size: 0.6rem;
		color: #aaa;
	}
	.state {
		padding: 2rem;
		text-align: center;
		color: var(--muted);
	}
	footer {
		padding: 0.8rem;
	}
	footer a {
		display: flex;
		justify-content: center;
		gap: 0.35rem;
		color: inherit;
		text-decoration: none;
		font-size: 0.68rem;
		font-weight: 650;
	}
	@media (max-width: 520px) {
		.popover {
			position: fixed;
			left: 1rem;
			right: 1rem;
			top: 78px;
			width: auto;
		}
	}
</style>
