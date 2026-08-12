package chirag127.memoria.data.ai

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AiEnricherTest {

    /** A provider that returns canned JSON — no network. */
    private fun providerReturning(json: String) =
        object : LlmProvider {
            override val config =
                ProviderConfig(
                    id = "fake",
                    displayName = "Fake",
                    baseUrl = "http://x/",
                    apiKeyAlias = null,
                    defaultModel = "m",
                    caps = setOf(Modality.TEXT),
                    tier = CostTier.FREE,
                    priority = 0,
                )

            override fun supports(task: TaskKind) = true

            override suspend fun complete(req: CompletionRequest) =
                Result.success(CompletionResponse(json, "m", "fake", 1))
        }

    @Test
    fun `parses well-formed extraction json`() =
        runTest {
            val json =
                """{"title":"Rust talk","type":"youtube","tags":["rust"],
            "summary":"good","entities":[{"kind":"concept","name":"ownership","canonical":"Rust Ownership"}],
            "tasks":[],"links":["Rust Ownership"]}"""
            val out =
                AiEnricher(AiRouter(listOf(providerReturning(json)))).enrich("watched a rust video")
            assertEquals("Rust talk", out.title)
            assertEquals("youtube", out.type)
            assertEquals("Rust Ownership", out.entities.single().canonical)
        }

    @Test
    fun `falls back to title-from-first-line when ai returns junk`() =
        runTest {
            val out =
                AiEnricher(AiRouter(listOf(providerReturning("not json at all"))))
                    .enrich("First line here\nsecond line")
            assertEquals("First line here", out.title)
            assertEquals("inbox", out.type)
        }

    @Test
    fun `falls back when no provider succeeds`() =
        runTest {
            val failing =
                object : LlmProvider {
                    override val config =
                        ProviderConfig(
                            id = "f",
                            displayName = "F",
                            baseUrl = "http://x/",
                            apiKeyAlias = null,
                            defaultModel = "m",
                            caps = setOf(Modality.TEXT),
                            tier = CostTier.FREE,
                            priority = 0,
                        )

                    override fun supports(task: TaskKind) = true

                    override suspend fun complete(req: CompletionRequest) =
                        Result.failure<CompletionResponse>(RuntimeException("boom"))
                }
            val out = AiEnricher(AiRouter(listOf(failing))).enrich("Meeting notes")
            assertTrue(out.title.isNotBlank())
        }
}
