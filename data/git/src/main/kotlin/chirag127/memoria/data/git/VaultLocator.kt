package chirag127.memoria.data.git

import chirag127.memoria.core.datastore.SettingsStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/** Resolves the vault working directory from settings; null until the user picks one. */
@Singleton
class VaultLocator
@Inject
constructor(
    private val settings: SettingsStore,
) {
    fun vaultDir(): File? =
        runBlocking { settings.vaultPath.first() }?.let { File(it) }?.takeIf { it.isDirectory }
}

/**
 * Builds the JGit engine on demand from the resolved vault dir. Reports
 * not-configured (rather than crashing) when no vault is set yet — keeps the
 * capture path working locally even before git is configured.
 */
class LazyGitVaultEngine(
    private val locator: VaultLocator,
    private val auth: GitAuthProvider,
) : GitVaultEngine {

    private fun engine(): GitVaultEngine? =
        locator.vaultDir()?.let { JGitVaultEngine(it, auth) }

    override fun stageAndCommit(relativePaths: List<String>, message: String) {
        engine()?.stageAndCommit(relativePaths, message)
    }

    override fun sync(): SyncResult =
        engine()?.sync() ?: SyncResult.AuthFailed("vault not configured — pick a vault folder in Settings")
}
