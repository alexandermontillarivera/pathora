import { link as routerLink } from "@dvcol/svelte-simple-router/action"

type LinkOptions = Parameters<typeof routerLink>[1]

const navigationOptions = (options?: LinkOptions): LinkOptions => ({
	stripQuery: true,
	...options,
})

/** Keep direct-entry query params, but never inherit them between internal routes. */
export function link(node: HTMLElement, options?: LinkOptions) {
	const action = routerLink(node, navigationOptions(options))
	return {
		update(next?: LinkOptions) { action.update?.(navigationOptions(next)) },
		destroy() { action.destroy?.() },
	}
}
