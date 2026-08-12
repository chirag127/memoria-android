package chirag127.memoria.data.ai

import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * One OpenAI-compatible provider impl for ALL providers — differs only by
 * [ProviderConfig] (+ optional shim). Resolves the API key by alias at call time.
 */
class OpenAiCompatProvider(
    override val config: ProviderConfig,
    private val http: HttpClient,
    private val keyLookup: (alias: String) -> String?,
    private val clockMs: () -> Long,
) : LlmProvider {
    override fun supports(task: TaskKind): Boolean =
        when (task) {
            TaskKind.TRANSCRIBE -> Modality.AUDIO_TRANSCRIBE in config.caps
            TaskKind.EMBED -> Modality.EMBEDDING in config.caps
            else -> Modality.TEXT in config.caps
        }

    override suspend fun complete(req: CompletionRequest): Result<CompletionResponse> =
        runCatching {
            val start = clockMs()
            val url = config.baseUrl.trimEnd('/') + "/chat/completions"
            val payload =
                OaiRequest(
                    model = req.model ?: config.defaultModel,
                    messages = req.messages.map { OaiMessage(it.role, it.content) },
                    temperature = req.temperature,
                    maxTokens = req.maxTokens,
                )
            val resp =
                http.post(url) {
                    contentType(ContentType.Application.Json)
                    headers {
                        config.apiKeyAlias?.let { alias ->
                            val key = keyLookup(alias) ?: error("missing key for ${config.id}")
                            append(HttpHeaders.Authorization, "Bearer $key")
                        }
                    }
                    setBody(JSON.encodeToString(OaiRequest.serializer(), payload))
                }
            if (!resp.status.isSuccess()) {
                error("${config.id} HTTP ${resp.status.value}: ${resp.bodyAsText().take(200)}")
            }
            val parsed = JSON.decodeFromString(OaiResponse.serializer(), resp.bodyAsText())
            CompletionResponse(
                text = parsed.choices.firstOrNull()?.message?.content.orEmpty(),
                model = parsed.model ?: payload.model,
                provider = config.id,
                latencyMs = clockMs() - start,
            )
        }

    private companion object {
        val JSON =
            Json {
                ignoreUnknownKeys = true
                encodeDefaults = false
            }
    }
}

@Serializable
private data class OaiRequest(
    val model: String,
    val messages: List<OaiMessage>,
    val temperature: Double,
    @SerialName("max_tokens") val maxTokens: Int? = null,
)

@Serializable
private data class OaiMessage(val role: String, val content: String)

@Serializable
private data class OaiResponse(
    val model: String? = null,
    val choices: List<OaiChoice> = emptyList(),
)

@Serializable
private data class OaiChoice(val message: OaiMessage)
