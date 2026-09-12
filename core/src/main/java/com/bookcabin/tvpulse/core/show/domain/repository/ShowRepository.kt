package com.bookcabin.tvpulse.core.show.domain.repository

import com.bookcabin.tvpulse.core.show.domain.model.Show
import kotlinx.coroutines.flow.Flow

interface ShowRepository {
    fun observeShows(): Flow<List<Show>>
    suspend fun refreshShows()
}
