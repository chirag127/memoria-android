package chirag127.memoria.data.vault

import chirag127.memoria.domain.model.Memory
import chirag127.memoria.domain.model.MemoryType
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Routes a [Memory] to its vault path. High-volume types are date-sharded
 * (YYYY/MM); bounded entity sets (people/companies/concepts) are flat so
 * [[wikilink]] autocomplete + dedup stay simple. Below-confidence captures
 * fall to 00-Inbox for human triage.
 */
object OrganizationEngine {
    fun route(memory: Memory): String {
        val d = memory.created.toLocalDateTime(TimeZone.currentSystemDefault()).date
        val shard = "%04d/%02d".format(d.year, d.monthNumber)
        val slug = slugify(memory.title).take(60)
        val dated = "$d-$slug.md"
        return when (memory.type) {
            MemoryType.JOURNAL -> "01-Journal/$shard/$dated"
            MemoryType.YOUTUBE -> "02-Learning/Youtube/$shard/$dated"
            MemoryType.ARTICLE -> "02-Learning/Articles/$shard/$dated"
            MemoryType.BOOK -> "02-Learning/Books/$slug.md"
            MemoryType.MEETING -> "03-Meetings/$shard/$dated"
            MemoryType.RESEARCH -> "04-Research/$dated"
            MemoryType.TASK -> "09-Tasks/open/$dated"
            MemoryType.HEALTH -> "10-Health/$shard/$dated"
            MemoryType.FINANCE -> "11-Finance/$shard/$dated"
            MemoryType.PERSON -> "06-People/$slug.md"
            MemoryType.COMPANY -> "07-Companies/$slug.md"
            MemoryType.CONCEPT -> "08-Concepts/$slug.md"
            MemoryType.INBOX -> "00-Inbox/$dated"
        }
    }

    fun slugify(s: String): String =
        s.lowercase()
            .replace(Regex("[^a-z0-9\\s-]"), "")
            .trim()
            .replace(Regex("\\s+"), "-")
            .replace(Regex("-+"), "-")
            .ifEmpty { "untitled" }
}
