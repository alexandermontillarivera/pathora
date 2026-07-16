import type { ChatCompletionMessageParam } from "@mlc-ai/web-llm"
import { buildCatalogContext } from "@lib/ai/catalog-context"
import type { MLCEngineInterface } from "@mlc-ai/web-llm"
import { get } from "svelte/store"
import {
	LOCAL_AI_MODEL,
	localAi,
	persistLocalAiPreference,
	updateLocalAi,
} from "@stores/local-ai"

let engine: MLCEngineInterface | null = null
let enginePromise: Promise<MLCEngineInterface> | null = null

function supportsWebGpu() {
	return window.isSecureContext && "gpu" in navigator
}

function friendlyError(cause: unknown) {
	const message = cause instanceof Error ? cause.message : String(cause)
	if (/memory|alloc|device lost|out of memory/i.test(message))
		return "El dispositivo no tiene suficiente memoria gráfica para este modelo."
	if (/webgpu|gpu/i.test(message))
		return "WebGPU no está disponible o está desactivado en este navegador."
	return "No pudimos preparar el modelo local. Revisa tu conexión e inténtalo nuevamente."
}

export async function inspectLocalAiCache() {
	const { hasModelInCache } = await import("@mlc-ai/web-llm")
	const cached = await hasModelInCache(LOCAL_AI_MODEL)
	updateLocalAi({ cached })
	return cached
}

export async function ensureLocalAiEngine() {
	if (engine) return engine
	if (enginePromise) return enginePromise
	if (!supportsWebGpu()) {
		updateLocalAi({
			status: "unsupported",
			error:
				"Necesitas un navegador compatible con WebGPU y una conexión HTTPS.",
		})
		throw new Error("WebGPU is not supported.")
	}
	if (!get(localAi).cached) await inspectLocalAiCache().catch(() => false)

	updateLocalAi({
		status: "downloading",
		progress: 0,
		progressText: get(localAi).cached
			? "Preparando el modelo guardado…"
			: "Iniciando descarga privada…",
		error: "",
	})
	enginePromise = (async () => {
		try {
			const { CreateWebWorkerMLCEngine } = await import("@mlc-ai/web-llm")
			const worker = new Worker(
				new URL("./local-ai.worker.ts", import.meta.url),
				{
					type: "module",
				},
			)
			engine = await CreateWebWorkerMLCEngine(worker, LOCAL_AI_MODEL, {
				initProgressCallback: (report) =>
					updateLocalAi({
						progress: report.progress,
						progressText: report.text,
					}),
				logLevel: "WARN",
			})
			updateLocalAi({
				status: "ready",
				cached: true,
				progress: 1,
				progressText: "Modelo listo en este dispositivo.",
			})
			return engine
		} catch (cause) {
			engine = null
			updateLocalAi({ status: "error", error: friendlyError(cause) })
			throw cause
		} finally {
			enginePromise = null
		}
	})()
	return enginePromise
}

export async function enableLocalAi() {
	persistLocalAiPreference(true)
	updateLocalAi({ enabled: true, status: "idle", error: "" })
	try {
		await ensureLocalAiEngine()
	} catch (cause) {
		persistLocalAiPreference(false)
		updateLocalAi({ enabled: false })
		throw cause
	}
}

export async function disableLocalAi() {
	persistLocalAiPreference(false)
	engine?.interruptGenerate()
	if (engine) await engine.unload()
	engine = null
	updateLocalAi({ enabled: false, status: "disabled", progress: 0, error: "" })
}

export async function deleteLocalAiModel() {
	await disableLocalAi()
	const { deleteModelAllInfoInCache } = await import("@mlc-ai/web-llm")
	await deleteModelAllInfoInCache(LOCAL_AI_MODEL)
	updateLocalAi({ cached: false, progressText: "" })
}

export function stopLocalAiGeneration() {
	engine?.interruptGenerate()
}

export async function streamLocalAiAnswer(
	question: string,
	history: ChatCompletionMessageParam[],
	onToken: (answer: string) => void,
) {
	const activeEngine = await ensureLocalAiEngine()
	const context = await buildCatalogContext(question)
	const system: ChatCompletionMessageParam = {
		role: "system",
		content: `Eres P, el orientador académico local y privado de Pathora. Responde siempre en español claro y cercano. Usa exclusivamente el catálogo proporcionado para afirmar datos sobre carreras, escuelas, modalidades, duración y pensums. No inventes programas ni materias. Puedes comparar opciones, hacer preguntas breves para conocer intereses y recomendar entre 2 y 4 carreras explicando por qué. Aclara cuando una decisión requiere orientación profesional adicional. Sé conciso: máximo 350 palabras.\n\n${context}`,
	}
	const messages = [
		system,
		...history.slice(-8),
		{ role: "user" as const, content: question },
	]
	const chunks = await activeEngine.chat.completions.create({
		messages,
		stream: true,
		temperature: 0.45,
		top_p: 0.9,
		max_tokens: 550,
	})
	let answer = ""
	for await (const chunk of chunks) {
		answer += chunk.choices[0]?.delta?.content ?? ""
		onToken(answer)
	}
	return answer
}
