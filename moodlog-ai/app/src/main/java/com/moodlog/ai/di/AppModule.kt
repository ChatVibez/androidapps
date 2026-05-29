package com.moodlog.ai.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.moodlog.ai.data.local.CachedInsightDao
import com.moodlog.ai.data.local.MoodEntryDao
import com.moodlog.ai.data.local.MoodLogDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val SETTINGS_STORE = "moodlog_settings"

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MoodLogDatabase =
        Room.databaseBuilder(context, MoodLogDatabase::class.java, MoodLogDatabase.DB_NAME)
            // OK during MVP: schema bump from v1 -> v2 (cached_insights). User has not
            // shipped yet, and cached_insights is regenerable. Replace with a real
            // migration once the app ships to production.
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideMoodEntryDao(db: MoodLogDatabase): MoodEntryDao = db.moodEntryDao()

    @Provides
    fun provideCachedInsightDao(db: MoodLogDatabase): CachedInsightDao = db.cachedInsightDao()

    @Provides
    @Singleton
    fun provideSettingsDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile(SETTINGS_STORE) }
        )
}
