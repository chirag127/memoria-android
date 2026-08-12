package chirag127.memoria.core.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Keystore-backed storage for the GitHub PAT + AI provider keys, addressed by
 * alias. Values are encrypted at rest (AES256-GCM) with a master key in the
 * Android Keystore. Never logged, never written to the vault or committed.
 */
@Singleton
class KeyVault
    @Inject
    constructor(@ApplicationContext context: Context) {
        private val prefs by lazy {
            val masterKey =
                MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()
            EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
            )
        }

        fun get(alias: String): String? = prefs.getString(alias, null)

        fun put(alias: String, value: String) = prefs.edit().putString(alias, value).apply()

        fun remove(alias: String) = prefs.edit().remove(alias).apply()

        fun has(alias: String): Boolean = prefs.contains(alias)

        companion object {
            private const val PREFS_NAME = "memoria_keys"
            const val ALIAS_GITHUB_PAT = "github_pat"
        }
    }
