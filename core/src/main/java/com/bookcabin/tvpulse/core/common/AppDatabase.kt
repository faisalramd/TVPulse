package com.bookcabin.tvpulse.core.common

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bookcabin.tvpulse.core.favorite.data.model.FavoriteEntity
import com.bookcabin.tvpulse.core.favorite.data.source.FavoriteDao
import com.bookcabin.tvpulse.core.show.data.model.ShowEntity
import com.bookcabin.tvpulse.core.show.data.source.ShowDao

@Database(entities = [ShowEntity::class, FavoriteEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun homeDao(): ShowDao
    abstract fun favoriteDao(): FavoriteDao
}
