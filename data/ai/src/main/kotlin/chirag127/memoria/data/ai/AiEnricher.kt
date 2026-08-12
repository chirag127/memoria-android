package chirag127.memoria.data.ai

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/** Structured result of AI enrichment — the knowledge-extraction contract. */
@Serializable
data class Extraction(
    val title: String,
    val type: String = "inbox",
    val tags: List<String> = emptyList(),
    val summary: String = "",
    val entities: List<ExtractedEntity> = emptyList(),
    val tasks: List<ExtractedTask> = emptyList(),
    val links: List<String> = emptyList(),
)

@Serializable
data class ExtractedEntity(val kind: String, val name: String, val canonical: String)

@Serializable
data class ExtractedTask(val text: String, val assignee: String? = null, val due: String? = null)

/**
 * Enriches a raw capture into a structured [Extraction] using the AI failover
 * chain. Falls back to a minimal Extraction (title = first line) if AI fails or
 * returns unparseable JSON — capture is never lost.
 */
class AiEnricher(private val router: AiRouter) {

    suspend fun enrich(text: String): Extraction {
        val prompt =
            buildString {
                append("Extract structured knowledge from this capture. Respond with ONLY JSON: ")
                append("{\"title\":str,\"type\":one of")
                append("[journal,youtube,article,book,meeting,research,task,health,finance,")
                append("person,company,concept,inbox],")
                append("\"tags\":[str],\"summary\":str,")
                append("\"entities\":[{\"kind\":str,\"name\":str,\"canonical\":str}],")
                append("\"tasks\":[{\"text\":str,\"assignee\":str|null,\"due\":str|null}],")
                append("\"links\":[str]}.\n\nCapture:\n")
                append(text)
            }
        val result =
            router.complete(
                CompletionRequest(
                    messages = listOf(Message("user", prompt)),
                    task = TaskKind.EXTRACT_ENTITIES,
                ),
            )
        return result.map { parse(it.text) ?: fallback(text) }.getOrElse { fallback(text) }
    }

    /** Returns null on unparseable output so the caller falls back to the original capture text. */
    private fun parse(raw: String): Extraction? {
        if (!raw.contains('{')) return null
        val json = raw.substringAfter('{').substringBeforeLast('}').let { "{$it}" }
        return runCatching { JSON.decodeFromString(Extraction.serializer(), json) }
            .getOrNull()
            ?.takeIf { it.title.isNotBlank() }
    }

    private fun fallback(text: String) =
        Extraction(
            title =
                text.trim().lineSequence().firstOrNull()?.take(80)?.ifBlank { "Untitled capture" }
                    ?: "Untitled capture",
            type = "inbox",
            summary = text.trim().take(280),
        )

    private companion object {
        val JSON = Json { ignoreUnknownKeys = true; isLenient = true }
    }
}
