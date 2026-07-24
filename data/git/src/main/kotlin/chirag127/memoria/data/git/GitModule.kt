package chirag127.memoria.data.git

import chirag127.memoria.core.security.KeyVault
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Auth provider backed by the Keystore-encrypted PAT store. Returns null until the
 * user configures a token in Settings (sync then reports AuthFailed).
 */
@Singleton
class KeyVaultGitAuthProvider @javax.inject.Inject constructor(
    private val keyVault: KeyVault,
) : GitAuthProvider {
    override fun token(): String? = keyVault.get(KeyVault.ALIAS_GITHUB_PAT)
}

@Module
@InstallIn(SingletonComponent::class)
object GitModule {
    @Provides
    @Singleton
    fun provideAuthProvider(keyVault: KeyVault): GitAuthProvider = KeyVaultGitAuthProvider(keyVault)

    /**
     * The engine needs a resolved vault working dir. Until the user picks a vault
     * (SAF) we can't construct a JGit repo, so the engine is created lazily by
     * VaultLocator; here we bind a resolver-backed engine.
     */
    @Provides
    @Singleton
    fun provideGitVaultEngine(
        locator: VaultLocator,
        auth: GitAuthProvider,
    ): GitVaultEngine = LazyGitVaultEngine(locator, auth)
}
