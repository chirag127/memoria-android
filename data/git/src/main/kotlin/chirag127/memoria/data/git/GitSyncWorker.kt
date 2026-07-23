package chirag127.memoria.data.git

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Drains the commit queue and syncs to the remote vault. Network-constrained,
 * exponential backoff (scheduled by SyncScheduler). Commits are already made
 * locally at capture time; this worker only needs the network for push.
 *
 * TODO: inject CommitQueue (Room) + GitVaultEngine; drain QUEUED->COMMITTED->PUSHED.
 */
@HiltWorker
class GitSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val engine: GitVaultEngine,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = when (val r = engine.sync()) {
        is SyncResult.Success, SyncResult.NoChanges -> Result.success()
        is SyncResult.AuthFailed -> Result.failure() // surfaced to Settings via a separate signal
        is SyncResult.Conflict -> Result.success()    // sidecar written; no retry needed
        is SyncResult.Retryable -> Result.retry()
    }

    companion object {
        const val UNIQUE_WORK = "memoria-git-sync"
    }
}
