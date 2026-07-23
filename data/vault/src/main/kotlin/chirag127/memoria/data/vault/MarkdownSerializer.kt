package chirag127.memoria.data.vault

import chirag127.memoria.domain.model.Memory

/**
 * Renders a [Memory] to an Obsidian markdown file: YAML frontmatter (base schema)
 * + body. Entities become both frontmatter fields and [[wikilinks]]. Deterministic
 * output so tests can assert byte content.
 */
class MarkdownSerializer {
    fun render(memory: Memory): String = buildString {
        appendLine("---")
        appendLine("id: ${memory.id}")
        appendLine("title: ${yaml(memory.title)}")
        appendLine("type: ${memory.type.name.lowercase()}")
        appendLine("created: ${memory.created}")
        appendLine("modified: ${memory.modified}")
        appendLine("source: ${memory.source.name.lowercase()}")
        if (memory.tags.isNotEmpty()) appendLine("tags: [${memory.tags.joinToString(", ")}]")
        if (memory.entities.isNotEmpty()) {
            appendLine("entities: [${memory.entities.joinToString(", ") { it.canonical }}]")
        }
        // typed entity lists → wikilinks
        memory.entities.groupBy { it.kind }.forEach { (kind, list) ->
            val field = when (kind) {
                "person" -> "people"; "company" -> "companies"; "concept" -> "concepts"
                else -> kind + "s"
            }
            appendLine("$field: [${list.joinToString(", ") { "\"[[${it.canonical}]]\"" }}]")
        }
        if (memory.links.isNotEmpty()) {
            appendLine("links: [${memory.links.joinToString(", ") { "\"[[$it]]\"" }}]")
        }
        appendLine("status: active")
        appendLine("---")
        appendLine()
        memory.summary?.let { appendLine("## Summary"); appendLine(it); appendLine() }
        if (memory.body.isNotBlank()) appendLine(memory.body)
        if (memory.actionItems.isNotEmpty()) {
            appendLine()
            appendLine("## Action Items")
            memory.actionItems.forEach { appendLine("- [ ] ${it.text}") }
        }
    }

    private fun yaml(v: String): String =
        if (v.any { it in ":#[]{}\",'" }) "\"${v.replace("\"", "\\\"")}\"" else v
}
