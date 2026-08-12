package chirag127.memoria.data.git

/** Result of a push attempt — surfaces auth failures so the UI can route to Settings. */
sealed interface SyncResult {
    data object Success : SyncResult

    data object NoChanges : SyncResult

    data class AuthFailed(val message: String) : SyncResult

    data class Conflict(val sidecarPath: String) : SyncResult

    data class Retryable(val cause: Throwable) : SyncResult
}

/**
 * Git operations over the vault working tree. One narrow surface, thick JGit impl.
 * All calls are blocking/IO — callers dispatch on IO / run inside a Worker.
 */
interface GitVaultEngine {
    fun stageAndCommit(relativePaths: List<String>, message: String)

    /** Pull (rebase) then push. Token resolved from Keystore by the impl. */
    fun sync(): SyncResult
}
