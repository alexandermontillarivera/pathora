import countries from "i18n-iso-countries"
import spanish from "i18n-iso-countries/langs/es.json"

countries.registerLocale(spanish)

const collator = new Intl.Collator("es", { sensitivity: "base" })
const countryNames = countries.getNames("es", { select: "official" })

export function countryName(countryCode?: string | null) {
	if (!countryCode) return "País no indicado"
	const code = countryCode.trim().toUpperCase()
	if (code === "OTHER") return "Otro país"
	return countryNames[code] ?? countryCode
}

export function formatCountry(countryCode?: string | null) {
	return countryName(countryCode)
}

export const countryOptions = [
	{ value: "", label: "Selecciona tu país" },
	...Object.entries(countryNames)
		.map(([code, name]) => ({
			value: code,
			label: name,
		}))
		.sort((left, right) => collator.compare(left.label, right.label)),
]

export function detectCountry() {
	if (typeof navigator === "undefined") return ""

	for (const language of navigator.languages ?? [navigator.language]) {
		try {
			const region = new Intl.Locale(language).region?.toUpperCase()
			if (region && countryNames[region]) return region
		} catch {
			// Ignore malformed browser locale values and try the next one.
		}
	}

	return ""
}
