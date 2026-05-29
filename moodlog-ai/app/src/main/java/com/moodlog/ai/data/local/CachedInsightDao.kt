package com.moodlog.ai.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CachedInsightDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(insight: CachedInsight)

    @Query("SELECT * FROM cached_insights WHERE id = 1 LIMIT 1")
    suspend fun get(): CachedInsight?

    @Query("DELETE FROM cached_insights")
    suspend fun clear()
}
