package chirag127.memoria.data.git

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Drains the commit queue then syncs to the remote vault. Commits are made
 * locally (offline-safe); only push needs the network. Network-constrained +
 * exponential backoff are set by the scheduler.
 */
@HiltWorker
class GitSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val engine: GitVaultEngine,
    private val queue: CommitQueue,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val pending = queue.pending()
        // Commit each queued item locally (safe offline).
        pending.filter { it.state == "QUEUED" }.forEach {
            runCatching { engine.stageAndCommit(listOf(it.vaultPath), it.message) }
        }
        return when (engine.sync()) {
            is SyncResult.Success, SyncResult.NoChanges -> {
                pending.forEach { queue.markPushed(it.id) }
                queue.clearPushed()
                Result.success()
            }
            is SyncResult.AuthFailed -> Result.failure()
            is SyncResult.Conflict -> Result.success()
            is SyncResult.Retryable -> Result.retry()
        }
    }

    companion object {
        const val UNIQUE_WORK = "memoria-git-sync"
    }
}
