package com.bookcabin.tvpulse.core.favorite.domain.repository

import com.bookcabin.tvpulse.core.favorite.domain.model.Favorite
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun observeFavorites(): Flow<List<Favorite>>
    fun observeIsFavorite(id: Int): Flow<Boolean>
    suspend fun addFavorite(favorite: Favorite)
    suspend fun removeFavorite(id: Int)
}
