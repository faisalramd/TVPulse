package com.bookcabin.tvpulse.core.favorite.data.repository

import com.bookcabin.tvpulse.core.favorite.data.model.FavoriteEntity
import com.bookcabin.tvpulse.core.favorite.data.source.FavoriteDao
import com.bookcabin.tvpulse.core.favorite.domain.model.Favorite
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FavoriteRepositoryImplTest {

    private class FakeFavoriteDao : FavoriteDao {
        val favorites = MutableStateFlow<Map<Int, FavoriteEntity>>(emptyMap())

        override fun getAllFavorites(): Flow<List<FavoriteEntity>> =
            favorites.map { it.values.sortedBy { entity -> entity.title.lowercase() } }

        override fun isFavorite(id: Int): Flow<Boolean> = favorites.map { id in it }

        override suspend fun insertFavorite(favorite: FavoriteEntity) {
            favorites.update { it + (favorite.id to favorite) }
        }

        override suspend fun deleteFavorite(id: Int) {
            favorites.update { it - id }
        }
    }

    private val dao = FakeFavoriteDao()
    private val repository = FavoriteRepositoryImpl(dao)

    private val strangerThings = Favorite(id = 2993, imageUrl = "st.jpg", title = "Stranger Things", genre = "Drama, Horror")
    private val breakingBad = Favorite(id = 169, imageUrl = "bb.jpg", title = "Breaking Bad", genre = "Drama, Crime")

    @Test
    fun `added favorites are observed as domain models`() = runTest {
        repository.addFavorite(strangerThings)
        repository.addFavorite(breakingBad)

        assertEquals(listOf(breakingBad, strangerThings), repository.observeFavorites().first())
    }

    @Test
    fun `adding the same show twice keeps a single entry`() = runTest {
        repository.addFavorite(strangerThings)
        repository.addFavorite(strangerThings.copy(genre = "Drama"))

        assertEquals(listOf(strangerThings.copy(genre = "Drama")), repository.observeFavorites().first())
    }

    @Test
    fun `observeIsFavorite switches from true to false after removal`() = runTest {
        repository.addFavorite(strangerThings)
        assertTrue(repository.observeIsFavorite(2993).first())

        repository.removeFavorite(2993)

        assertFalse(repository.observeIsFavorite(2993).first())
        assertTrue(repository.observeFavorites().first().isEmpty())
    }
}
