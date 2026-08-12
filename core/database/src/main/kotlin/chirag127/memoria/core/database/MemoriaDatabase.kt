package chirag127.memoria.core.database

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(memory: MemoryEntity)

    @Query("SELECT * FROM memory ORDER BY createdEpochMs DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memory WHERE id = :id")
    suspend fun get(id: String): MemoryEntity?

    @Query(
        """
        SELECT * FROM memory
        WHERE title LIKE '%' || :q || '%'
           OR summary LIKE '%' || :q || '%'
        ORDER BY createdEpochMs DESC
        LIMIT :limit
        """,
    )
    suspend fun search(
        q: String,
        limit: Int,
    ): List<MemoryEntity>
}

@Dao
interface PendingCommitDao {
    @Insert
    suspend fun enqueue(commit: PendingCommitEntity): Long

    @Query("SELECT * FROM pending_commit WHERE state != 'PUSHED' ORDER BY enqueuedEpochMs ASC")
    suspend fun pending(): List<PendingCommitEntity>

    @Query("UPDATE pending_commit SET state = :state WHERE id = :id")
    suspend fun setState(
        id: Long,
        state: String,
    )

    @Query("DELETE FROM pending_commit WHERE state = 'PUSHED'")
    suspend fun clearPushed()
}

@Database(
    entities = [MemoryEntity::class, PendingCommitEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class MemoriaDatabase : RoomDatabase() {
    abstract fun memoryDao(): MemoryDao

    abstract fun pendingCommitDao(): PendingCommitDao
}
