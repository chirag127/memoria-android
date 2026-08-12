package chirag127.memoria.domain.model

import kotlinx.datetime.Instant

/** Kind of a captured memory — drives vault folder + note type. */
enum class MemoryType {
    JOURNAL,
    YOUTUBE,
    ARTICLE,
    BOOK,
    MEETING,
    RESEARCH,
    TASK,
    HEALTH,
    FINANCE,
    PERSON,
    COMPANY,
    CONCEPT,
    INBOX,
}

/** Where a capture originated. */
enum class CaptureSourceKind { VOICE, MANUAL, SHARE, YOUTUBE, WEB, CALENDAR, IMPORT, PHOTO }

/** An extracted entity (person/company/concept/…) with a canonical name for dedup. */
data class Entity(val kind: String, val name: String, val canonical: String)

/** An extracted actionable item. */
data class ActionItem(val text: String, val assignee: String? = null, val due: String? = null)

/**
 * The immutable core of a memory. `id` is a stable ULID-ish key; the vault filename
 * is derived but mutable — links survive renames because they resolve by title/id.
 */
data class Memory(
    val id: String,
    val title: String,
    val type: MemoryType,
    val created: Instant,
    val modified: Instant,
    val tags: List<String> = emptyList(),
    val source: CaptureSourceKind,
    val summary: String? = null,
    val body: String = "",
    val entities: List<Entity> = emptyList(),
    val actionItems: List<ActionItem> = emptyList(),
    val links: List<String> = emptyList(),
    val extra: Map<String, String> = emptyMap(),
)
