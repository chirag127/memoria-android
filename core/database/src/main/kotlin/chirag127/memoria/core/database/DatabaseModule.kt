package chirag127.memoria.core.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MemoriaDatabase =
        Room.databaseBuilder(context, MemoriaDatabase::class.java, "memoria.db")
            .fallbackToDestructiveMigration() // cache — SoT is the git vault
            .build()

    @Provides
    fun provideMemoryDao(db: MemoriaDatabase): MemoryDao = db.memoryDao()

    @Provides
    fun providePendingCommitDao(db: MemoriaDatabase): PendingCommitDao = db.pendingCommitDao()
}
