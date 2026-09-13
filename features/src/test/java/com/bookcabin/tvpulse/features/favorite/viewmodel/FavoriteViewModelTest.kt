package com.bookcabin.tvpulse.features.favorite.viewmodel

import com.bookcabin.tvpulse.core.favorite.domain.model.Favorite
import com.bookcabin.tvpulse.core.favorite.domain.repository.FavoriteRepository
import com.bookcabin.tvpulse.core.favorite.domain.usecase.GetFavoritesUseCase
import com.bookcabin.tvpulse.core.favorite.domain.usecase.RemoveFavoriteUseCase
import com.bookcabin.tvpulse.features.R
import com.bookcabin.tvpulse.features.common.error.ErrorMessage
import com.bookcabin.tvpulse.features.common.state.UiState
import com.bookcabin.tvpulse.features.testutil.FakeFavoriteRepository
import com.bookcabin.tvpulse.features.testutil.MainDispatcherRule
import com.bookcabin.tvpulse.features.testutil.TestData.strangerThingsFavorite
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val breakingBad = Favorite(id = 169, imageUrl = "https://img/169.jpg", title = "Breaking Bad", genre = "Drama, Crime")

    private fun TestScope.createViewModel(repository: FavoriteRepository): FavoriteViewModel {
        val viewModel = FavoriteViewModel(
            getFavoritesUseCase = GetFavoritesUseCase(repository),
            removeFavoriteUseCase = RemoveFavoriteUseCase(repository)
        )
        // uiState uses WhileSubscribed, so it only updates while something collects it.
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        return viewModel
    }

    @Test
    fun `starts in Loading state`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = createViewModel(FakeFavoriteRepository())

        assertEquals(UiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `saved favorites are Success`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = createViewModel(FakeFavoriteRepository(listOf(strangerThingsFavorite, breakingBad)))
        advanceUntilIdle()

        assertEquals(UiState.Success(listOf(breakingBad, strangerThingsFavorite)), viewModel.uiState.value)
    }

    @Test
    fun `no favorites is Empty`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = createViewModel(FakeFavoriteRepository())
        advanceUntilIdle()

        assertEquals(UiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun `removeFavorite updates the list`() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeFavoriteRepository(listOf(strangerThingsFavorite, breakingBad))
        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        viewModel.removeFavorite(breakingBad.id)
        advanceUntilIdle()

        assertEquals(UiState.Success(listOf(strangerThingsFavorite)), viewModel.uiState.value)
        assertEquals(setOf(strangerThingsFavorite.id), repository.favorites.value.keys)
    }

    @Test
    fun `removing the last favorite switches to Empty`() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeFavoriteRepository(listOf(strangerThingsFavorite))
        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        viewModel.removeFavorite(strangerThingsFavorite.id)
        advanceUntilIdle()

        assertEquals(UiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun `a failing favorites source is Error with mapped message`() = runTest(mainDispatcherRule.testDispatcher) {
        val failingRepository = object : FavoriteRepository by FakeFavoriteRepository() {
            override fun observeFavorites(): Flow<List<Favorite>> = flow { throw IOException("disk error") }
        }

        val viewModel = createViewModel(failingRepository)
        advanceUntilIdle()

        assertEquals(UiState.Error(ErrorMessage(R.string.error_network)), viewModel.uiState.value)
    }
}
