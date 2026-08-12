package chirag127.memoria.data.vault

import chirag127.memoria.domain.model.CaptureSourceKind
import chirag127.memoria.domain.model.Entity
import chirag127.memoria.domain.model.Memory
import chirag127.memoria.domain.model.MemoryType
import kotlinx.datetime.Instant
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class VaultWriterTest {

    private fun sample() =
        Memory(
            id = "20260724T0914-a3f9",
            title = "Rust Ownership Explained",
            type = MemoryType.YOUTUBE,
            created = Instant.parse("2026-07-24T09:14:03Z"),
            modified = Instant.parse("2026-07-24T09:14:03Z"),
            tags = listOf("learning/rust"),
            source = CaptureSourceKind.YOUTUBE,
            summary = "Ownership makes memory safety compile-time.",
            entities = listOf(Entity("concept", "ownership", "Rust Ownership")),
        )

    @Test
    fun `routes youtube capture to date-sharded learning folder`() {
        val path = OrganizationEngine.route(sample())
        assertEquals("02-Learning/Youtube/2026/07/2026-07-24-rust-ownership-explained.md", path)
    }

    @Test
    fun `writes markdown with frontmatter and wikilink entity`(@TempDir dir: File) {
        val rel = VaultWriter(dir).write(sample())
        val written = File(dir, rel).readText()
        assertTrue(written.startsWith("---"), "has frontmatter")
        assertTrue("id: 20260724T0914-a3f9" in written, "has stable id")
        assertTrue("concepts: [\"[[Rust Ownership]]\"]" in written, "entity -> wikilink")
        assertTrue("## Summary" in written, "has summary section")
    }

    @Test
    fun `entity types route flat, events route dated`() {
        val person = sample().copy(type = MemoryType.PERSON, title = "Ravi Kumar")
        assertEquals("06-People/ravi-kumar.md", OrganizationEngine.route(person))
    }
}
