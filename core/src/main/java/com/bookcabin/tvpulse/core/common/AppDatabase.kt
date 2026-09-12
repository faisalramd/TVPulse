package com.bookcabin.tvpulse.core.common

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bookcabin.tvpulse.core.show.data.model.ShowEntity
import com.bookcabin.tvpulse.core.show.data.source.ShowDao

@Database(entities = [ShowEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun homeDao(): ShowDao
}
