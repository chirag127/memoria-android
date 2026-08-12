package chirag127.memoria.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Cached mirror of a vault note for fast UI + search. Source of truth is the git vault. */
@Entity(tableName = "memory")
data class MemoryEntity(
    @PrimaryKey val id: String,
    val title: String,
    val type: String,
    val createdEpochMs: Long,
    val modifiedEpochMs: Long,
    // comma-joined
    val tags: String,
    val source: String,
    val summary: String?,
    // relative path in the vault
    val vaultPath: String,
)

/** A pending git commit, survives process death; drained by GitSyncWorker. */
@Entity(tableName = "pending_commit")
data class PendingCommitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vaultPath: String,
    val message: String,
    // QUEUED | COMMITTED | PUSHED
    val state: String,
    val enqueuedEpochMs: Long,
)
