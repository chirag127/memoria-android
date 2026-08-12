package chirag127.memoria.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "memoria_settings")

/**
 * Non-secret app settings (secrets live in :core:security KeyVault). Holds the
 * vault working-dir path, the git remote URL, and the last-pushed commit SHA.
 */
@Singleton
class SettingsStore
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        val vaultPath: Flow<String?> = context.dataStore.data.map { it[KEY_VAULT_PATH] }

        val remoteUrl: Flow<String?> = context.dataStore.data.map { it[KEY_REMOTE_URL] }

        val lastPushedSha: Flow<String?> = context.dataStore.data.map { it[KEY_LAST_SHA] }

        suspend fun setVaultPath(path: String) = context.dataStore.edit { it[KEY_VAULT_PATH] = path }

        suspend fun setRemoteUrl(url: String) = context.dataStore.edit { it[KEY_REMOTE_URL] = url }

        suspend fun setLastPushedSha(sha: String) = context.dataStore.edit { it[KEY_LAST_SHA] = sha }

        private companion object {
            val KEY_VAULT_PATH = stringPreferencesKey("vault_path")
            val KEY_REMOTE_URL = stringPreferencesKey("remote_url")
            val KEY_LAST_SHA = stringPreferencesKey("last_pushed_sha")
        }
    }
