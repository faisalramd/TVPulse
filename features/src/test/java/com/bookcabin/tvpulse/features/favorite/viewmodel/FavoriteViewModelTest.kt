package com.bookcabin.tvpulse.features.favorite.viewmodel

import com.bookcabin.tvpulse.core.favorite.domain.model.Favorite
import com.bookcabin.tvpulse.core.favorite.domain.usecase.GetFavoritesUseCase
import com.bookcabin.tvpulse.core.favorite.domain.usecase.RemoveFavoriteUseCase
import com.bookcabin.tvpulse.features.testutil.FakeFavoriteRepository
import com.bookcabin.tvpulse.features.testutil.MainDispatcherRule
import com.bookcabin.tvpulse.features.testutil.TestData.strangerThingsFavorite
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val breakingBad = Favorite(id = 169, imageUrl = "https://img/169.jpg", title = "Breaking Bad", genre = "Drama, Crime")

    private fun TestScope.createViewModel(repository: FakeFavoriteRepository): FavoriteViewModel {
        val viewModel = FavoriteViewModel(
            getFavoritesUseCase = GetFavoritesUseCase(repository),
            removeFavoriteUseCase = RemoveFavoriteUseCase(repository)
        )
        // uiState uses WhileSubscribed, so it only updates while something collects it.
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        return viewModel
    }

    @Test
    fun `starts in loading state`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = createViewModel(FakeFavoriteRepository())

        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `shows saved favorites`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = createViewModel(FakeFavoriteRepository(listOf(strangerThingsFavorite, breakingBad)))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(listOf(breakingBad, strangerThingsFavorite), state.favorites)
    }

    @Test
    fun `empty repository gives an empty, loaded list`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = createViewModel(FakeFavoriteRepository())
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.favorites.isEmpty())
    }

    @Test
    fun `removeFavorite removes the item from the list`() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeFavoriteRepository(listOf(strangerThingsFavorite, breakingBad))
        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        viewModel.removeFavorite(breakingBad.id)
        advanceUntilIdle()

        assertEquals(listOf(strangerThingsFavorite), viewModel.uiState.value.favorites)
        assertEquals(setOf(strangerThingsFavorite.id), repository.favorites.value.keys)
    }
}
