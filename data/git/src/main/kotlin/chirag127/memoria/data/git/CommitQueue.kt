package chirag127.memoria.data.git

import chirag127.memoria.core.database.PendingCommitDao
import chirag127.memoria.core.database.PendingCommitEntity
import javax.inject.Inject

/**
 * Durable queue of pending vault commits (Room-backed, survives process death).
 * Capture enqueues; GitSyncWorker drains.
 */
class CommitQueue
@Inject
constructor(
    private val dao: PendingCommitDao,
) {
    suspend fun enqueue(vaultPath: String, message: String) {
        dao.enqueue(
            PendingCommitEntity(
                vaultPath = vaultPath,
                message = message,
                state = "QUEUED",
                enqueuedEpochMs = System.currentTimeMillis(),
            ),
        )
    }

    suspend fun pending(): List<PendingCommitEntity> = dao.pending()

    suspend fun markPushed(id: Long) = dao.setState(id, "PUSHED")

    suspend fun clearPushed() = dao.clearPushed()
}
