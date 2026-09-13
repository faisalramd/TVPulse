package com.bookcabin.tvpulse.core.favorite.data.repository

import com.bookcabin.tvpulse.core.favorite.data.mapper.toDomain
import com.bookcabin.tvpulse.core.favorite.data.mapper.toEntity
import com.bookcabin.tvpulse.core.favorite.data.source.FavoriteDao
import com.bookcabin.tvpulse.core.favorite.domain.model.Favorite
import com.bookcabin.tvpulse.core.favorite.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteDao: FavoriteDao
) : FavoriteRepository {

    override fun observeFavorites(): Flow<List<Favorite>> =
        favoriteDao.getAllFavorites().map { entities -> entities.map { it.toDomain() } }

    override fun observeIsFavorite(id: Int): Flow<Boolean> = favoriteDao.isFavorite(id)

    override suspend fun addFavorite(favorite: Favorite) {
        favoriteDao.insertFavorite(favorite.toEntity())
    }

    override suspend fun removeFavorite(id: Int) {
        favoriteDao.deleteFavorite(id)
    }
}
