package com.bookcabin.tvpulse.di

import android.content.Context
import androidx.room.Room
import com.bookcabin.tvpulse.core.common.AppDatabase
import com.bookcabin.tvpulse.core.common.SettingsDataStore
import com.bookcabin.tvpulse.core.show.data.source.ShowDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "tvpulse_db"
        )
            // The shows table is a network cache, so dropping it on schema changes is safe.
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    @Provides
    fun provideHomeDao(database: AppDatabase): ShowDao {
        return database.homeDao()
    }

    @Provides
    @Singleton
    fun provideSettingsDataStore(@ApplicationContext context: Context): SettingsDataStore {
        return SettingsDataStore(context)
    }
}
