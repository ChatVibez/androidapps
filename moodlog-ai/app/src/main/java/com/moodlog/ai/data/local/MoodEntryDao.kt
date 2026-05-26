package com.moodlog.ai.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: MoodEntry): Long

    @Query("SELECT * FROM mood_entries ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<MoodEntry>>

    @Query("SELECT * FROM mood_entries WHERE createdAt >= :sinceMillis ORDER BY createdAt ASC")
    suspend fun getSince(sinceMillis: Long): List<MoodEntry>

    @Query("DELETE FROM mood_entries WHERE id = :id")
    suspend fun deleteById(id: Long)
}
