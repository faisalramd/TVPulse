package com.bookcabin.tvpulse.features.home.viewmodel

import com.bookcabin.tvpulse.core.show.domain.usecase.GetLocalShowsUseCase
import com.bookcabin.tvpulse.core.show.domain.usecase.RefreshShowsUseCase
import com.bookcabin.tvpulse.core.show.domain.usecase.SearchShowsUseCase
import com.bookcabin.tvpulse.features.R
import com.bookcabin.tvpulse.features.common.error.ErrorMessage
import com.bookcabin.tvpulse.features.common.state.UiState
import com.bookcabin.tvpulse.features.home.constant.HomeConstants
import com.bookcabin.tvpulse.features.testutil.FakeShowRepository
import com.bookcabin.tvpulse.features.testutil.MainDispatcherRule
import com.bookcabin.tvpulse.features.testutil.TestData
import com.bookcabin.tvpulse.features.testutil.TestData.girls
import com.bookcabin.tvpulse.features.testutil.TestData.personOfInterest
import com.bookcabin.tvpulse.features.testutil.TestData.underTheDome
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.net.UnknownHostException

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeShowRepository()

    private val noInternet = ErrorMessage(R.string.error_no_internet)

    private fun TestScope.createViewModel(): HomeViewModel {
        val viewModel = HomeViewModel(
            getLocalShowsUseCase = GetLocalShowsUseCase(repository),
            refreshShowsUseCase = RefreshShowsUseCase(repository),
            searchShowsUseCase = SearchShowsUseCase(repository)
        )
        // uiState uses WhileSubscribed, so it only updates while something collects it.
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        return viewModel
    }

    @Test
    fun `starts in Loading state`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = createViewModel()

        assertEquals(UiState.Loading, viewModel.uiState.value.showsState)
    }

    @Test
    fun `stays Loading while the first refresh runs with an empty cache`() =
        runTest(mainDispatcherRule.testDispatcher) {
            repository.refreshResult = { awaitCancellation() }

            val viewModel = createViewModel()
            runCurrent()

            assertEquals(UiState.Loading, viewModel.uiState.value.showsState)
        }

    @Test
    fun `refresh loads the configured number of shows as Success`() = runTest(mainDispatcherRule.testDispatcher) {
        repository.refreshResult = { listOf(underTheDome, personOfInterest) }

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(listOf<Int?>(HomeConstants.SHOW_ITEMS_LIMIT), repository.refreshLimits)
        assertEquals(UiState.Success(listOf(underTheDome, personOfInterest)), viewModel.uiState.value.showsState)
    }

    @Test
    fun `refresh with no shows gives Empty`() = runTest(mainDispatcherRule.testDispatcher) {
        repository.refreshResult = { emptyList() }

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(UiState.Empty, viewModel.uiState.value.showsState)
    }

    @Test
    fun `refresh failure with empty cache gives Error and shows the dialog`() =
        runTest(mainDispatcherRule.testDispatcher) {
            repository.refreshResult = { throw UnknownHostException() }

            val viewModel = createViewModel()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertEquals(UiState.Error(noInternet), state.showsState)
            assertEquals(noInternet, state.errorMessage)
        }

    @Test
    fun `refresh failure with cached shows keeps Success and still shows the dialog`() =
        runTest(mainDispatcherRule.testDispatcher) {
            repository.localShows.value = listOf(underTheDome)
            repository.refreshResult = { throw UnknownHostException() }

            val viewModel = createViewModel()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertEquals(UiState.Success(listOf(underTheDome)), state.showsState)
            assertEquals(noInternet, state.errorMessage)
        }

    @Test
    fun `search is debounced and only the last query is sent`() = runTest(mainDispatcherRule.testDispatcher) {
        repository.searchResult = { listOf(girls) }
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onQueryChange("gir")
        advanceTimeBy(HomeConstants.SEARCH_DEBOUNCE_MS / 2)
        viewModel.onQueryChange("girls")
        advanceTimeBy(HomeConstants.SEARCH_DEBOUNCE_MS - 1)
        runCurrent()

        assertTrue(repository.searchQueries.isEmpty())
        assertEquals(UiState.Loading, viewModel.uiState.value.showsState)

        advanceTimeBy(1)
        runCurrent()

        assertEquals(listOf("girls"), repository.searchQueries)
    }

    @Test
    fun `search results are Success`() = runTest(mainDispatcherRule.testDispatcher) {
        repository.refreshResult = { listOf(underTheDome) }
        repository.searchResult = { listOf(girls) }
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onQueryChange("girls")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("girls", state.query)
        assertEquals(UiState.Success(listOf(girls)), state.showsState)
    }

    @Test
    fun `search with no results is Empty`() = runTest(mainDispatcherRule.testDispatcher) {
        repository.searchResult = { emptyList() }
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onQueryChange("zzqq")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(UiState.Empty, state.showsState)
        assertNull(state.errorMessage)
    }

    @Test
    fun `search failure is Error with mapped message and shows the dialog`() =
        runTest(mainDispatcherRule.testDispatcher) {
            repository.searchResult = { throw TestData.httpException(503) }
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.onQueryChange("girls")
            advanceUntilIdle()

            val serverError = ErrorMessage(R.string.error_server, listOf(503))
            val state = viewModel.uiState.value
            assertEquals(UiState.Error(serverError), state.showsState)
            assertEquals(serverError, state.errorMessage)
        }

    @Test
    fun `clearing the query shows the local list again`() = runTest(mainDispatcherRule.testDispatcher) {
        repository.refreshResult = { listOf(underTheDome) }
        repository.searchResult = { listOf(girls) }
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onQueryChange("girls")
        advanceUntilIdle()
        viewModel.onQueryChange("")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("", state.query)
        assertEquals(UiState.Success(listOf(underTheDome)), state.showsState)
    }

    @Test
    fun `retry with empty query refreshes again`() = runTest(mainDispatcherRule.testDispatcher) {
        repository.refreshResult = { throw UnknownHostException() }
        val viewModel = createViewModel()
        advanceUntilIdle()

        repository.refreshResult = { listOf(underTheDome) }
        viewModel.retry()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, repository.refreshLimits.size)
        assertEquals(UiState.Success(listOf(underTheDome)), state.showsState)
        assertNull(state.errorMessage)
    }

    @Test
    fun `retry with a query searches again without waiting for debounce`() =
        runTest(mainDispatcherRule.testDispatcher) {
            repository.searchResult = { throw UnknownHostException() }
            val viewModel = createViewModel()
            advanceUntilIdle()
            viewModel.onQueryChange("girls")
            advanceUntilIdle()

            repository.searchResult = { listOf(girls) }
            viewModel.retry()
            runCurrent()

            val state = viewModel.uiState.value
            assertEquals(listOf("girls", "girls"), repository.searchQueries)
            assertEquals(UiState.Success(listOf(girls)), state.showsState)
            assertNull(state.errorMessage)
        }

    @Test
    fun `dismissError hides the dialog but keeps the Error content`() = runTest(mainDispatcherRule.testDispatcher) {
        repository.refreshResult = { throw UnknownHostException() }
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.dismissError()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.errorMessage)
        assertEquals(UiState.Error(noInternet), state.showsState)
    }
}
