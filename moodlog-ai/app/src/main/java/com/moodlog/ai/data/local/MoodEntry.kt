package com.moodlog.ai.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a single mood check-in entry.
 *
 * @param moodScore 1-10 self-reported scale (1 = sangat buruk, 10 = sangat baik).
 * @param emoji shorthand emoji selection from the picker.
 * @param tags comma-separated context tags (e.g. "kerja,keluarga").
 * @param journal optional free-form journal text.
 * @param createdAt epoch millis when the entry was saved.
 */
@Entity(tableName = "mood_entries")
data class MoodEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val moodScore: Int,
    val emoji: String,
    val tags: String = "",
    val journal: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
