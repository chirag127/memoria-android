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
        val DEFAULTS = listOf(
            ProviderConfig("pollinations", "Pollinations", "https://text.pollinations.ai/openai/", null, "openai", caps = setOf(Modality.TEXT, Modality.VISION), tier = CostTier.FREE, priority = 1),
            ProviderConfig("openrouter-free", "OpenRouter (free)", "https://openrouter.ai/api/v1/", "openrouter", "meta-llama/llama-3.3-70b-instruct:free", caps = setOf(Modality.TEXT), tier = CostTier.FREE, priority = 2),
            ProviderConfig("ollama", "Ollama (local)", "http://localhost:11434/v1/", null, "llama3.1", caps = setOf(Modality.TEXT), tier = CostTier.FREE, priority = 3),
            ProviderConfig("gemini", "Gemini", "https://generativelanguage.googleapis.com/v1beta/openai/", "gemini", "gemini-2.0-flash", transcribeModel = "gemini-2.0-flash", caps = setOf(Modality.TEXT, Modality.AUDIO_TRANSCRIBE, Modality.VISION), tier = CostTier.PAID, priority = 4),
            ProviderConfig("claude", "Claude", "https://api.anthropic.com/v1/", "claude", "claude-sonnet-latest", caps = setOf(Modality.TEXT, Modality.VISION), tier = CostTier.PAID, priority = 5),
            ProviderConfig("openai", "OpenAI", "https://api.openai.com/v1/", "openai", "gpt-4o", transcribeModel = "whisper-1", caps = setOf(Modality.TEXT, Modality.AUDIO_TRANSCRIBE, Modality.EMBEDDING), tier = CostTier.PAID, priority = 6),
        )
    }
}

interface LlmProvider {
    val config: ProviderConfig
    fun supports(task: TaskKind): Boolean
    suspend fun complete(req: CompletionRequest): Result<CompletionResponse>
}
