package com.moodlog.ai.di

import android.content.Context
import androidx.room.Room
import com.moodlog.ai.data.local.MoodEntryDao
import com.moodlog.ai.data.local.MoodLogDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MoodLogDatabase =
        Room.databaseBuilder(context, MoodLogDatabase::class.java, MoodLogDatabase.DB_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideMoodEntryDao(db: MoodLogDatabase): MoodEntryDao = db.moodEntryDao()
}
