const ALLOWED_TAGS = new Set([
	"A",
	"B",
	"BLOCKQUOTE",
	"BR",
	"EM",
	"I",
	"LI",
	"OL",
	"P",
	"STRONG",
	"U",
	"UL",
])

const BLOCKED_TAGS = new Set([
	"BUTTON",
	"EMBED",
	"FORM",
	"IFRAME",
	"INPUT",
	"MATH",
	"OBJECT",
	"SCRIPT",
	"STYLE",
	"SVG",
	"TEMPLATE",
])

const escapeHtml = (value: string) =>
	value
		.replaceAll("&", "&amp;")
		.replaceAll("<", "&lt;")
		.replaceAll(">", "&gt;")
		.replaceAll('"', "&quot;")

function safeLink(value: string) {
	if (value.startsWith("/") || value.startsWith("#")) return value
	try {
		const url = new URL(value)
		return ["http:", "https:", "mailto:"].includes(url.protocol) ? value : null
	} catch {
		return null
	}
}

function cleanElement(element: Element) {
	if (BLOCKED_TAGS.has(element.tagName)) {
		element.remove()
		return
	}

	for (const child of [...element.children]) cleanElement(child)

	if (!ALLOWED_TAGS.has(element.tagName)) {
		element.replaceWith(...element.childNodes)
		return
	}

	const href = element.tagName === "A" ? element.getAttribute("href") : null
	for (const attribute of [...element.attributes]) element.removeAttribute(attribute.name)

	if (element.tagName === "A" && href) {
		const safeHref = safeLink(href)
		if (safeHref) {
			element.setAttribute("href", safeHref)
			element.setAttribute("rel", "nofollow noopener noreferrer")
			if (!safeHref.startsWith("/") && !safeHref.startsWith("#"))
				element.setAttribute("target", "_blank")
		}
	}
}

export function richTextHtml(content: unknown) {
	let source = ""
	if (typeof content === "string") source = content
	else if (
		content &&
		typeof content === "object" &&
		"html" in content &&
		typeof (content as { html?: unknown }).html === "string"
	)
		source = (content as { html: string }).html

	if (!source.trim()) return ""
	if (typeof DOMParser === "undefined") return `<p>${escapeHtml(source)}</p>`

	const document = new DOMParser().parseFromString(`<div>${source}</div>`, "text/html")
	const root = document.body.firstElementChild
	if (!root) return ""
	for (const child of [...root.children]) cleanElement(child)

	const hasMarkup = /<\/?[a-z][\s\S]*>/i.test(source)
	return hasMarkup
		? root.innerHTML
		: `<p>${escapeHtml(source).replaceAll("\n", "<br>")}</p>`
}
