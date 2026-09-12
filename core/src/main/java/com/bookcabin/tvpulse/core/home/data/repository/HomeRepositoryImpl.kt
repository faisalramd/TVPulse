package com.bookcabin.tvpulse.core.home.data.repository

import com.bookcabin.tvpulse.core.home.data.mapper.toDomain
import com.bookcabin.tvpulse.core.home.data.mapper.toEntity
import com.bookcabin.tvpulse.core.home.data.source.HomeApi
import com.bookcabin.tvpulse.core.home.data.source.HomeDao
import com.bookcabin.tvpulse.core.home.domain.model.Show
import com.bookcabin.tvpulse.core.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val homeApi: HomeApi,
    private val homeDao: HomeDao
) : HomeRepository {

    override fun observeShows(): Flow<List<Show>> =
        homeDao.getAllShows().map { entities -> entities.map { it.toDomain() } }

    override suspend fun refreshShows() {
        val shows = homeApi.getShows().map { it.toDomain() }
        homeDao.insertShows(shows.map { it.toEntity() })
    }
}
