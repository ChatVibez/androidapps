package com.moodlog.ai.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Single-row cache for the latest AI weekly insight.
 *
 * The cache is considered valid when:
 * - generated less than 24 hours ago AND
 * - the underlying entries' content hash matches [entriesHash]
 *
 * Invalid cache forces a fresh Gemini call.
 */
@Entity(tableName = "cached_insights")
data class CachedInsight(
    @PrimaryKey val id: Int = SINGLETON_ID,
    val content: String,
    val generatedAt: Long,
    val entriesHash: Int
) {
    companion object {
        const val SINGLETON_ID = 1
    }
}
