package com.moodlog.ai.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: MoodEntry): Long

    @Update
    suspend fun update(entry: MoodEntry)

    @Query("SELECT * FROM mood_entries WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): MoodEntry?

    @Query("SELECT * FROM mood_entries ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<MoodEntry>>

    @Query("SELECT * FROM mood_entries WHERE createdAt >= :sinceMillis ORDER BY createdAt ASC")
    suspend fun getSince(sinceMillis: Long): List<MoodEntry>

    @Query("DELETE FROM mood_entries WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM mood_entries")
    suspend fun deleteAll()
}
