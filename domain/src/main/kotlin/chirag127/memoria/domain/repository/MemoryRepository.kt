package chirag127.memoria.domain.repository

import chirag127.memoria.domain.model.Memory
import kotlinx.coroutines.flow.Flow

/** A raw capture before AI enrichment. */
data class RawCapture(
    val text: String? = null,
    val audioPath: String? = null,
    val imagePath: String? = null,
    val url: String? = null,
    val sourceKind: chirag127.memoria.domain.model.CaptureSourceKind,
)

/**
 * The app's spine: ingest a raw capture, enrich via AI, organize + write to the
 * vault, and queue a git commit. Implementations live in :data:repository.
 */
interface MemoryRepository {
    /** Enrich + organize + write to vault + enqueue commit. Returns the persisted memory id. */
    suspend fun capture(raw: RawCapture): Result<String>

    /** Cached memories for UI (source of truth is the git vault; this reads Room). */
    fun observeRecent(limit: Int = 50): Flow<List<Memory>>

    suspend fun get(id: String): Memory?
}

/** Full-text + (later) semantic search over the cached vault index. */
interface SearchRepository {
    suspend fun search(query: String, limit: Int = 50): List<Memory>
}
