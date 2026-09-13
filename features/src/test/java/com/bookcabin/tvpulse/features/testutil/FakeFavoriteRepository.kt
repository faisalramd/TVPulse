package com.bookcabin.tvpulse.features.testutil

import com.bookcabin.tvpulse.core.favorite.domain.model.Favorite
import com.bookcabin.tvpulse.core.favorite.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

// In-memory stand-in for the Room-backed repository.
class FakeFavoriteRepository(initial: List<Favorite> = emptyList()) : FavoriteRepository {

    val favorites = MutableStateFlow(initial.associateBy { it.id })

    override fun observeFavorites(): Flow<List<Favorite>> = favorites.map { it.values.sortedBy(Favorite::title) }

    override fun observeIsFavorite(id: Int): Flow<Boolean> = favorites.map { id in it }

    override suspend fun addFavorite(favorite: Favorite) {
        favorites.update { it + (favorite.id to favorite) }
    }

    override suspend fun removeFavorite(id: Int) {
        favorites.update { it - id }
    }
}
