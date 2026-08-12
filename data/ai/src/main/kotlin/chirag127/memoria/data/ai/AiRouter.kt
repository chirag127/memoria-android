package chirag127.memoria.data.ai

/**
 * Walks the failover chain for a task: filters providers by capability, sorts
 * free-first (tier then priority) — unless [CompletionRequest.preferQuality] flips
 * to the paid-quality order. Tries each until one succeeds.
 *
 * TODO: per-provider CircuitBreaker (open on 401/402, threshold on 429/5xx) +
 * RouteLog for cost/latency-aware ordering + daily budget cap.
 */
class AiRouter(private val providers: List<LlmProvider>) {
    fun chainFor(req: CompletionRequest): List<LlmProvider> {
        val eligible = providers.filter { it.config.enabled && it.supports(req.task) }
        return if (req.preferQuality) {
            eligible.sortedWith(
                compareByDescending<LlmProvider> { it.config.tier.ordinal }.thenBy { it.config.priority },
            )
        } else {
            eligible.sortedWith(
                compareBy<LlmProvider> { it.config.tier.ordinal }.thenBy { it.config.priority },
            )
        }
    }

    suspend fun complete(req: CompletionRequest): Result<CompletionResponse> {
        val chain = chainFor(req)
        if (chain.isEmpty()) return Result.failure(IllegalStateException("no provider for ${req.task}"))
        var last: Throwable? = null
        for (p in chain) {
            val r = p.complete(req)
            if (r.isSuccess) return r
            last = r.exceptionOrNull()
        }
        return Result.failure(AllProvidersFailed(chain.map { it.config.id }, last))
    }
}

class AllProvidersFailed(val tried: List<String>, cause: Throwable?) :
    Exception("all providers failed: $tried", cause)
