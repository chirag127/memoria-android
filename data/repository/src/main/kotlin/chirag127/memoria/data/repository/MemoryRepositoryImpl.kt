package chirag127.memoria.data.repository

import chirag127.memoria.core.database.MemoryDao
import chirag127.memoria.core.database.MemoryEntity
import chirag127.memoria.data.ai.AiEnricher
import chirag127.memoria.data.ai.Extraction
import chirag127.memoria.data.git.CommitQueue
import chirag127.memoria.data.git.SyncScheduler
import chirag127.memoria.data.git.VaultLocator
import chirag127.memoria.data.vault.VaultWriter
import chirag127.memoria.domain.model.CaptureSourceKind
import chirag127.memoria.domain.model.Entity
import chirag127.memoria.domain.model.Memory
import chirag127.memoria.domain.model.MemoryType
import chirag127.memoria.domain.repository.MemoryRepository
import chirag127.memoria.domain.repository.RawCapture
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.io.File
import javax.inject.Inject

/**
 * The spine: raw capture → AI enrich → build Memory → write markdown to the vault
 * → enqueue commit → schedule sync. UI reads from the Room cache.
 */
class MemoryRepositoryImpl @Inject constructor(
    private val enricher: AiEnricher,
    private val dao: MemoryDao,
    private val queue: CommitQueue,
    private val scheduler: SyncScheduler,
    private val locator: VaultLocator,
) : MemoryRepository {

    override suspend fun capture(raw: RawCapture): Result<String> = runCatching {
        val text = raw.text ?: raw.url ?: error("empty capture")
        val extraction = enricher.enrich(text)
        val now = Clock.System.now()
        val memory = Memory(
            id = defaultId(),
            title = extraction.title,
            type = extraction.type.toMemoryType(),
            created = now,
            modified = now,
            tags = extraction.tags,
            source = raw.sourceKind,
            summary = extraction.summary.ifBlank { null },
            body = raw.url?.let { "Source: $it\n\n$text" } ?: text,
            entities = extraction.entities.map { Entity(it.kind, it.name, it.canonical) },
            links = extraction.links,
        )

        val vaultDir = locator.vaultDir() ?: appLocalFallback()
        val relPath = VaultWriter(vaultDir).write(memory)

        dao.upsert(memory.toEntity(relPath))
        queue.enqueue(relPath, "capture: ${memory.title}")
        scheduler.requestSync()
        memory.id
    }

    override fun observeRecent(limit: Int): Flow<List<Memory>> =
        dao.observeRecent(limit).map { list -> list.map { it.toMemory() } }

    override suspend fun get(id: String): Memory? = dao.get(id)?.toMemory()

    // Until a vault is picked, write to an app-local vault so capture never fails.
    private fun appLocalFallback(): File =
        File(System.getProperty("java.io.tmpdir") ?: ".", "memoria-vault").apply { mkdirs() }

    private companion object {
        fun defaultId(): String {
            val t = System.currentTimeMillis()
            val rnd = (t % 100000).toString(36)
            return "cap-$t-$rnd"
        }
    }
}

private fun String.toMemoryType(): MemoryType =
    runCatching { MemoryType.valueOf(uppercase()) }.getOrDefault(MemoryType.INBOX)

private fun Memory.toEntity(vaultPath: String) = MemoryEntity(
    id = id,
    title = title,
    type = type.name.lowercase(),
    createdEpochMs = created.toEpochMilliseconds(),
    modifiedEpochMs = modified.toEpochMilliseconds(),
    tags = tags.joinToString(","),
    source = source.name.lowercase(),
    summary = summary,
    vaultPath = vaultPath,
)

private fun MemoryEntity.toMemory() = Memory(
    id = id,
    title = title,
    type = runCatching { MemoryType.valueOf(type.uppercase()) }.getOrDefault(MemoryType.INBOX),
    created = Instant.fromEpochMilliseconds(createdEpochMs),
    modified = Instant.fromEpochMilliseconds(modifiedEpochMs),
    tags = tags.split(",").filter { it.isNotBlank() },
    source = runCatching { CaptureSourceKind.valueOf(source.uppercase()) }.getOrDefault(CaptureSourceKind.MANUAL),
    summary = summary,
)
