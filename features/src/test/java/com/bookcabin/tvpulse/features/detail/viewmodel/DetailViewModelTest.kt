package com.bookcabin.tvpulse.features.detail.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.bookcabin.tvpulse.core.favorite.domain.usecase.AddFavoriteUseCase
import com.bookcabin.tvpulse.core.favorite.domain.usecase.IsFavoriteUseCase
import com.bookcabin.tvpulse.core.favorite.domain.usecase.RemoveFavoriteUseCase
import com.bookcabin.tvpulse.core.show.domain.usecase.GetShowDetailUseCase
import com.bookcabin.tvpulse.features.R
import com.bookcabin.tvpulse.features.common.error.ErrorMessage
import com.bookcabin.tvpulse.features.common.state.UiState
import com.bookcabin.tvpulse.features.navigation.Detail
import com.bookcabin.tvpulse.features.testutil.FakeFavoriteRepository
import com.bookcabin.tvpulse.features.testutil.FakeShowRepository
import com.bookcabin.tvpulse.features.testutil.MainDispatcherRule
import com.bookcabin.tvpulse.features.testutil.TestData
import com.bookcabin.tvpulse.features.testutil.TestData.strangerThings
import com.bookcabin.tvpulse.features.testutil.TestData.strangerThingsFavorite
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.net.UnknownHostException

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val showRepository = FakeShowRepository()
    private val favoriteRepository = FakeFavoriteRepository()

    private fun createViewModel(showId: Int = strangerThings.id) = DetailViewModel(
        savedStateHandle = SavedStateHandle(mapOf(Detail.SHOW_ID_KEY to showId)),
        getShowDetailUseCase = GetShowDetailUseCase(showRepository),
        isFavoriteUseCase = IsFavoriteUseCase(favoriteRepository),
        addFavoriteUseCase = AddFavoriteUseCase(favoriteRepository),
        removeFavoriteUseCase = RemoveFavoriteUseCase(favoriteRepository)
    )

    @Test
    fun `starts in Loading state`() {
        showRepository.detailResult = { strangerThings }

        val state = createViewModel().uiState.value

        assertEquals(UiState.Loading, state.detailState)
    }

    @Test
    fun `loads detail for the showId from navigation args`() = runTest(mainDispatcherRule.testDispatcher) {
        showRepository.detailResult = { strangerThings }

        val viewModel = createViewModel(showId = 2993)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(listOf(2993), showRepository.requestedDetailIds)
        assertEquals(UiState.Success(strangerThings), state.detailState)
        assertNull(state.errorMessage)
    }

    @Test
    fun `failure maps exception to a readable error`() = runTest(mainDispatcherRule.testDispatcher) {
        showRepository.detailResult = { throw UnknownHostException() }

        val viewModel = createViewModel()
        advanceUntilIdle()

        val noInternet = ErrorMessage(R.string.error_no_internet)
        val state = viewModel.uiState.value
        assertEquals(UiState.Error(noInternet), state.detailState)
        assertEquals(noInternet, state.errorMessage)
    }

    @Test
    fun `404 maps to not found error`() = runTest(mainDispatcherRule.testDispatcher) {
        showRepository.detailResult = { throw TestData.httpException(404) }

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(ErrorMessage(R.string.error_not_found), viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `retry recovers after a failure`() = runTest(mainDispatcherRule.testDispatcher) {
        showRepository.detailResult = { throw UnknownHostException() }
        val viewModel = createViewModel()
        advanceUntilIdle()

        showRepository.detailResult = { strangerThings }
        viewModel.retry()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(UiState.Success(strangerThings), state.detailState)
        assertNull(state.errorMessage)
        assertEquals(2, showRepository.requestedDetailIds.size)
    }

    @Test
    fun `dismissError clears the error without reloading`() = runTest(mainDispatcherRule.testDispatcher) {
        showRepository.detailResult = { throw UnknownHostException() }
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.dismissError()
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.errorMessage)
        assertTrue(viewModel.uiState.value.detailState is UiState.Error)
        assertEquals(1, showRepository.requestedDetailIds.size)
    }

    @Test
    fun `isFavorite is false when show is not saved`() = runTest(mainDispatcherRule.testDispatcher) {
        showRepository.detailResult = { strangerThings }

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isFavorite)
    }

    @Test
    fun `isFavorite is true when show is already saved`() = runTest(mainDispatcherRule.testDispatcher) {
        favoriteRepository.addFavorite(strangerThingsFavorite)
        showRepository.detailResult = { strangerThings }

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isFavorite)
    }

    @Test
    fun `toggleFavorite adds the loaded show as a favorite`() = runTest(mainDispatcherRule.testDispatcher) {
        showRepository.detailResult = { strangerThings }
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.toggleFavorite()
        advanceUntilIdle()

        assertEquals(mapOf(2993 to strangerThingsFavorite), favoriteRepository.favorites.value)
        assertTrue(viewModel.uiState.value.isFavorite)
    }

    @Test
    fun `toggleFavorite removes an existing favorite`() = runTest(mainDispatcherRule.testDispatcher) {
        favoriteRepository.addFavorite(strangerThingsFavorite)
        showRepository.detailResult = { strangerThings }
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.toggleFavorite()
        advanceUntilIdle()

        assertTrue(favoriteRepository.favorites.value.isEmpty())
        assertFalse(viewModel.uiState.value.isFavorite)
    }

    @Test
    fun `toggleFavorite does nothing when detail failed to load`() = runTest(mainDispatcherRule.testDispatcher) {
        showRepository.detailResult = { throw UnknownHostException() }
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.toggleFavorite()
        advanceUntilIdle()

        assertTrue(favoriteRepository.favorites.value.isEmpty())
    }

    @Test
    fun `isFavorite follows changes made elsewhere`() = runTest(mainDispatcherRule.testDispatcher) {
        favoriteRepository.addFavorite(strangerThingsFavorite)
        showRepository.detailResult = { strangerThings }
        val viewModel = createViewModel()
        advanceUntilIdle()

        // e.g. removed from the Favorite screen
        favoriteRepository.removeFavorite(2993)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isFavorite)
    }
}
