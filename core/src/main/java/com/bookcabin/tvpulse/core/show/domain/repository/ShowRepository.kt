package com.bookcabin.tvpulse.core.show.domain.repository

import com.bookcabin.tvpulse.core.show.domain.model.Show
import com.bookcabin.tvpulse.core.show.domain.model.ShowDetail
import kotlinx.coroutines.flow.Flow

interface ShowRepository {
    fun observeShows(): Flow<List<Show>>
    suspend fun refreshShows(limitItems: Int? = null)
    suspend fun searchShows(query: String): List<Show>
    suspend fun getShowDetail(id: Int): ShowDetail
}
