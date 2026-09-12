package com.bookcabin.tvpulse.core.common

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bookcabin.tvpulse.core.home.data.model.ShowEntity
import com.bookcabin.tvpulse.core.home.data.source.HomeDao

@Database(entities = [ShowEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun homeDao(): HomeDao
}
