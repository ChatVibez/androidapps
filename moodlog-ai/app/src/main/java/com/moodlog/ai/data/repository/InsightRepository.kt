package com.moodlog.ai.data.repository

import com.moodlog.ai.data.ai.GeminiService
import com.moodlog.ai.data.local.CachedInsight
import com.moodlog.ai.data.local.CachedInsightDao
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Result of an insight request, distinguishing fresh API calls from cache hits
 * so the UI can communicate freshness and offer a manual refresh.
 */
sealed interface InsightResult {
    data class Success(
        val content: String,
        val generatedAt: Long,
        val fromCache: Boolean
    ) : InsightResult

    data class Failure(val message: String) : InsightResult
}

/**
 * Wraps mood data + Gemini calls with a 24h cache to save quota.
 *
 * Cache invalidation rules:
 *  - age > [CACHE_TTL_MS] OR
 *  - the entries content hash differs from the cached one
 *  - [getInsight] called with `forceRefresh = true` (e.g. user taps refresh)
 */
@Singleton
class InsightRepository @Inject constructor(
    private val moodRepository: MoodRepository,
    private val cachedDao: CachedInsightDao,
    private val gemini: GeminiService
) {

    suspend fun getInsight(forceRefresh: Boolean = false): InsightResult {
        val sevenDaysAgo = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000
        val entries = moodRepository.entriesSince(sevenDaysAgo)
        val currentHash = entries.hashCode()

        if (!forceRefresh) {
            val cached = cachedDao.get()
            if (cached != null) {
                val ageMs = System.currentTimeMillis() - cached.generatedAt
                val freshEnough = ageMs in 0..CACHE_TTL_MS
                val sameContent = cached.entriesHash == currentHash
                if (freshEnough && sameContent) {
                    return InsightResult.Success(
                        content = cached.content,
                        generatedAt = cached.generatedAt,
                        fromCache = true
                    )
                }
            }
        }

        return gemini.weeklyInsight(entries).fold(
            onSuccess = { content ->
                val now = System.currentTimeMillis()
                cachedDao.upsert(
                    CachedInsight(
                        content = content,
                        generatedAt = now,
                        entriesHash = currentHash
                    )
                )
                InsightResult.Success(content = content, generatedAt = now, fromCache = false)
            },
            onFailure = { e ->
                InsightResult.Failure(e.message ?: "Unknown error")
            }
        )
    }

    suspend fun clearCache() = cachedDao.clear()

    companion object {
        const val CACHE_TTL_MS: Long = 24L * 60 * 60 * 1000
    }
}
