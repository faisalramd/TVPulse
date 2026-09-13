package com.bookcabin.tvpulse.core.show.data.repository

import com.bookcabin.tvpulse.core.show.data.mapper.toDomain
import com.bookcabin.tvpulse.core.show.data.mapper.toEntity
import com.bookcabin.tvpulse.core.show.data.source.ShowApi
import com.bookcabin.tvpulse.core.show.data.source.ShowDao
import com.bookcabin.tvpulse.core.show.domain.model.Show
import com.bookcabin.tvpulse.core.show.domain.repository.ShowRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ShowRepositoryImpl @Inject constructor(
    private val showApi: ShowApi,
    private val showDao: ShowDao
) : ShowRepository {

    override fun observeShows(): Flow<List<Show>> =
        showDao.getAllShows().map { entities -> entities.map { it.toDomain() } }

    override suspend fun refreshShows(limitItems: Int?) {
        val shows = showApi.getShows().toDomain(limitItems)
        showDao.insertShows(shows.map { it.toEntity() })
    }

    override suspend fun searchShows(query: String): List<Show> =
        showApi.searchShows(query).map { it.show.toDomain() }
}
