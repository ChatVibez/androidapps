package com.moodlog.ai.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [MoodEntry::class],
    version = 1,
    exportSchema = false
)
abstract class MoodLogDatabase : RoomDatabase() {
    abstract fun moodEntryDao(): MoodEntryDao

    companion object {
        const val DB_NAME = "moodlog.db"
    }
}
