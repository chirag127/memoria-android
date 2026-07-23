package chirag127.memoria.data.vault

import chirag127.memoria.domain.model.Memory
import java.io.File

/**
 * Writes a rendered memory into the vault working tree. The vault is the source of
 * truth; the returned relative path is what [chirag127.memoria.data.git] then
 * stages + commits. Writing is atomic (temp file + rename) so a crash never leaves
 * a half-written note.
 */
class VaultWriter(
    private val vaultRoot: File,
    private val serializer: MarkdownSerializer = MarkdownSerializer(),
) {
    /** Returns the vault-relative path of the written note. */
    fun write(memory: Memory): String {
        val relPath = OrganizationEngine.route(memory)
        val target = File(vaultRoot, relPath)
        target.parentFile?.mkdirs()
        val tmp = File(target.parentFile, target.name + ".tmp")
        tmp.writeText(serializer.render(memory))
        check(tmp.renameTo(target)) { "atomic rename failed for $relPath" }
        return relPath
    }
}
