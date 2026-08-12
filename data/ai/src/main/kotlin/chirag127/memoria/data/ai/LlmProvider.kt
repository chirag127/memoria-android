package chirag127.memoria.data.ai

enum class Modality { TEXT, AUDIO_TRANSCRIBE, EMBEDDING, VISION }

enum class CostTier { FREE, FREE_LIMITED, PAID }

enum class TaskKind { TRANSCRIBE, SUMMARIZE, EXTRACT_ENTITIES, EMBED, CHAT, TITLE }

data class Message(val role: String, val content: String)

data class CompletionRequest(
    val messages: List<Message>,
    val task: TaskKind,
    val model: String? = null,
    val temperature: Double = 0.2,
    val maxTokens: Int? = null,
    val preferQuality: Boolean = false,
)

data class CompletionResponse(
    val text: String,
    val model: String,
    val provider: String,
    val latencyMs: Long,
)

/**
 * Config-driven provider descriptor. Keys are NOT stored here — only [apiKeyAlias]
 * (resolved from Keystore-backed storage at request time). Every provider is the
 * same OpenAI-compatible impl differing only by this config (+ optional shim).
 */
data class ProviderConfig(
    val id: String,
    val displayName: String,
    val baseUrl: String,
    val apiKeyAlias: String?,
    val defaultModel: String,
    val transcribeModel: String? = null,
    val caps: Set<Modality>,
    val tier: CostTier,
    val priority: Int,
    val enabled: Boolean = true,
) {
    companion object {
        /** Free-first default roster (users edit / add via BYOK settings). */
        val DEFAULTS =
            listOf(
                ProviderConfig(
                    id = "pollinations",
                    displayName = "Pollinations",
                    baseUrl = "https://text.pollinations.ai/openai/",
                    apiKeyAlias = null,
                    defaultModel = "openai",
                    caps = setOf(Modality.TEXT, Modality.VISION),
                    tier = CostTier.FREE,
                    priority = 1,
                ),
                ProviderConfig(
                    id = "openrouter-free",
                    displayName = "OpenRouter (free)",
                    baseUrl = "https://openrouter.ai/api/v1/",
                    apiKeyAlias = "openrouter",
                    defaultModel = "meta-llama/llama-3.3-70b-instruct:free",
                    caps = setOf(Modality.TEXT),
                    tier = CostTier.FREE,
                    priority = 2,
                ),
                ProviderConfig(
                    id = "ollama",
                    displayName = "Ollama (local)",
                    baseUrl = "http://localhost:11434/v1/",
                    apiKeyAlias = null,
                    defaultModel = "llama3.1",
                    caps = setOf(Modality.TEXT),
                    tier = CostTier.FREE,
                    priority = 3,
                ),
                ProviderConfig(
                    id = "gemini",
                    displayName = "Gemini",
                    baseUrl = "https://generativelanguage.googleapis.com/v1beta/openai/",
                    apiKeyAlias = "gemini",
                    defaultModel = "gemini-2.0-flash",
                    transcribeModel = "gemini-2.0-flash",
                    caps = setOf(Modality.TEXT, Modality.AUDIO_TRANSCRIBE, Modality.VISION),
                    tier = CostTier.PAID,
                    priority = 4,
                ),
                ProviderConfig(
                    id = "claude",
                    displayName = "Claude",
                    baseUrl = "https://api.anthropic.com/v1/",
                    apiKeyAlias = "claude",
                    defaultModel = "claude-sonnet-latest",
                    caps = setOf(Modality.TEXT, Modality.VISION),
                    tier = CostTier.PAID,
                    priority = 5,
                ),
                ProviderConfig(
                    id = "openai",
                    displayName = "OpenAI",
                    baseUrl = "https://api.openai.com/v1/",
                    apiKeyAlias = "openai",
                    defaultModel = "gpt-4o",
                    transcribeModel = "whisper-1",
                    caps = setOf(Modality.TEXT, Modality.AUDIO_TRANSCRIBE, Modality.EMBEDDING),
                    tier = CostTier.PAID,
                    priority = 6,
                ),
            )
    }
}

interface LlmProvider {
    val config: ProviderConfig

    fun supports(task: TaskKind): Boolean

    suspend fun complete(req: CompletionRequest): Result<CompletionResponse>
}
