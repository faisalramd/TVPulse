package com.bookcabin.tvpulse.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ShowEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun showDao(): ShowDao
}
