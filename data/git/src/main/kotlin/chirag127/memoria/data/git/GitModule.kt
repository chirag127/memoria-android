package chirag127.memoria.data.git

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Wiring for the git-vault layer. The real GitVaultEngine needs a configured vault
 * root + auth token (set after the user picks a vault + PAT), so for the scaffold
 * we provide an engine that reports "not configured" until that wiring lands.
 *
 * TODO: replace with a provider that reads the vault path (DataStore) + token
 * (Keystore, :core:security) and builds JGitVaultEngine.
 */
@Module
@InstallIn(SingletonComponent::class)
object GitModule {
    @Provides
    @Singleton
    fun provideGitVaultEngine(): GitVaultEngine = NotConfiguredGitVaultEngine
}

internal object NotConfiguredGitVaultEngine : GitVaultEngine {
    override fun stageAndCommit(relativePaths: List<String>, message: String) {
        throw IllegalStateException("vault not configured — set repo + PAT in Settings")
    }

    override fun sync(): SyncResult =
        SyncResult.AuthFailed("vault not configured — set repo + PAT in Settings")
}
