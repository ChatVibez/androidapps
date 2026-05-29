package com.moodlog.ai.data.repository

import com.moodlog.ai.data.local.MoodEntry
import com.moodlog.ai.data.local.MoodEntryDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MoodRepository @Inject constructor(
    private val dao: MoodEntryDao
) {
    fun observeAll(): Flow<List<MoodEntry>> = dao.observeAll()

    suspend fun save(entry: MoodEntry): Long = dao.insert(entry)

    suspend fun update(entry: MoodEntry) = dao.update(entry)

    suspend fun getById(id: Long): MoodEntry? = dao.getById(id)

    suspend fun entriesSince(epochMillis: Long): List<MoodEntry> = dao.getSince(epochMillis)

    suspend fun delete(id: Long) = dao.deleteById(id)

    suspend fun deleteAll() = dao.deleteAll()
}
