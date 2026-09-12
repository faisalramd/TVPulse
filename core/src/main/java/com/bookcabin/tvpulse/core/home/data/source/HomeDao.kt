package com.bookcabin.tvpulse.core.home.data.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bookcabin.tvpulse.core.home.data.model.ShowEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeDao {
    @Query("SELECT * FROM shows")
    fun getAllShows(): Flow<List<ShowEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShows(shows: List<ShowEntity>)

    @Query("SELECT COUNT(*) FROM shows")
    suspend fun getShowCount(): Int
}
