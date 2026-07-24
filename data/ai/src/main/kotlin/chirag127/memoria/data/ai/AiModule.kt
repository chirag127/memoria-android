package chirag127.memoria.data.ai

import chirag127.memoria.core.security.KeyVault
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AiModule {
    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient = HttpClient(CIO)

    @Provides
    @Singleton
    fun provideRouter(http: HttpClient, keyVault: KeyVault): AiRouter {
        val providers = ProviderConfig.DEFAULTS.map { cfg ->
            OpenAiCompatProvider(
                config = cfg,
                http = http,
                keyLookup = { alias -> keyVault.get(alias) },
                clockMs = { System.currentTimeMillis() },
            )
        }
        return AiRouter(providers)
    }

    @Provides
    @Singleton
    fun provideEnricher(router: AiRouter): AiEnricher = AiEnricher(router)
}
