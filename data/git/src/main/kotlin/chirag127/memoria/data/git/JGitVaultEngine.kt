package chirag127.memoria.data.git

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.TransportException
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.io.File

/** Supplies the GitHub token (from Keystore-backed storage in :core:security). */
fun interface GitAuthProvider {
    /** Returns the PAT / installation token, or null if not configured. */
    fun token(): String?
}

/**
 * JGit-backed vault engine: commits locally (offline-safe), then pull --rebase +
 * push over HTTPS with a Keystore token. Per-memory timestamped filenames make
 * true content conflicts rare; non-fast-forward is resolved by rebase.
 *
 * TODO: wire ConflictResolver for the rare content-conflict case (last-writer-wins
 * + .conflict.md sidecar), and expose progress for the sync UI.
 */
class JGitVaultEngine(
    private val vaultRoot: File,
    private val auth: GitAuthProvider,
) : GitVaultEngine {
    private fun open(): Git = Git.open(vaultRoot)

    override fun stageAndCommit(relativePaths: List<String>, message: String) {
        open().use { git ->
            relativePaths.forEach { git.add().addFilepattern(it).call() }
            git.commit().setMessage(message).setSign(false).call()
        }
    }

    override fun sync(): SyncResult {
        val token = auth.token() ?: return SyncResult.AuthFailed("no token configured")
        val creds = UsernamePasswordCredentialsProvider("x-access-token", token)
        return try {
            open().use { git ->
                git.pull().setRebase(true).setCredentialsProvider(creds).call()
                val push = git.push().setCredentialsProvider(creds).call()
                // TODO: inspect RemoteRefUpdate status for rejected non-ff after rebase.
                SyncResult.Success
            }
        } catch (e: TransportException) {
            val msg = e.message ?: ""
            if ("auth" in msg.lowercase() || "401" in msg || "403" in msg) {
                SyncResult.AuthFailed(msg)
            } else {
                SyncResult.Retryable(e)
            }
        } catch (e: Exception) {
            SyncResult.Retryable(e)
        }
    }
}
