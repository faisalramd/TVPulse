package com.bookcabin.tvpulse.core.home.domain.repository

import com.bookcabin.tvpulse.core.home.domain.model.Show
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun observeShows(): Flow<List<Show>>
    suspend fun refreshShows()
}
