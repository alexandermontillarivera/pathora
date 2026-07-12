const escapeHtml = (value: string) =>
	value
		.replaceAll("&", "&amp;")
		.replaceAll("<", "&lt;")
		.replaceAll(">", "&gt;")
		.replaceAll('"', "&quot;")
export function markdownToHtml(markdown: string) {
	const lines = markdown.replaceAll("\r", "").split("\n")
	let html = ""
	let inList = false
	for (const raw of lines) {
		const line = raw.trim()
		if (!line) {
			if (inList) {
				html += "</ul>"
				inList = false
			}
			continue
		}
		if (line.startsWith("- ")) {
			if (!inList) {
				html += "<ul>"
				inList = true
			}
			html += `<li>${escapeHtml(line.slice(2))}</li>`
			continue
		}
		if (inList) {
			html += "</ul>"
			inList = false
		}
		if (line.startsWith("### ")) html += `<h3>${escapeHtml(line.slice(4))}</h3>`
		else if (line.startsWith("## "))
			html += `<h2>${escapeHtml(line.slice(3))}</h2>`
		else if (line.startsWith("> "))
			html += `<blockquote>${escapeHtml(line.slice(2))}</blockquote>`
		else html += `<p>${escapeHtml(line)}</p>`
	}
	if (inList) html += "</ul>"
	return html
}
export function markdownSummary(markdown: string) {
	return (
		markdown
			.replaceAll("\r", "")
			.split("\n")
			.map((line) => line.trim())
			.find(
				(line) =>
					line &&
					!line.startsWith("#") &&
					!line.startsWith("- ") &&
					!line.startsWith("> "),
			) ?? ""
	)
}
export function markdownOutcomes(markdown: string) {
	return markdown
		.replaceAll("\r", "")
		.split("\n")
		.map((line) => line.trim())
		.filter((line) => line.startsWith("- "))
		.map((line) => line.slice(2))
}
