package com.bookcabin.tvpulse.core.show.data.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bookcabin.tvpulse.core.show.data.model.ShowEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShowDao {
    @Query("SELECT * FROM shows")
    fun getAllShows(): Flow<List<ShowEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShows(shows: List<ShowEntity>)

    @Query("SELECT COUNT(*) FROM shows")
    suspend fun getShowCount(): Int
}
