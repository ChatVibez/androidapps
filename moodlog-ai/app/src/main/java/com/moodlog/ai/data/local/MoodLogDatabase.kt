package com.moodlog.ai.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [MoodEntry::class, CachedInsight::class],
    version = 2,
    exportSchema = false
)
abstract class MoodLogDatabase : RoomDatabase() {
    abstract fun moodEntryDao(): MoodEntryDao
    abstract fun cachedInsightDao(): CachedInsightDao

    companion object {
        const val DB_NAME = "moodlog.db"
    }
}
