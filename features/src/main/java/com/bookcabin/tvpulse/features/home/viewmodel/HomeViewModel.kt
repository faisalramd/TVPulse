package com.bookcabin.tvpulse.features.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookcabin.tvpulse.core.show.domain.usecase.GetLocalShowsUseCase
import com.bookcabin.tvpulse.core.show.domain.usecase.RefreshShowsUseCase
import com.bookcabin.tvpulse.core.show.domain.usecase.SearchShowsUseCase
import com.bookcabin.tvpulse.features.R
import com.bookcabin.tvpulse.features.home.constant.HomeConstants
import com.bookcabin.tvpulse.features.home.state.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getLocalShowsUseCase: GetLocalShowsUseCase,
    private val refreshShowsUseCase: RefreshShowsUseCase,
    private val searchShowsUseCase: SearchShowsUseCase
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val isRefreshing = MutableStateFlow(false)
    private val refreshFailed = MutableStateFlow(false)
    private val searchState = MutableStateFlow(HomeUiState())
    private val errorMessageRes = MutableStateFlow<Int?>(null)
    private var searchJob: Job? = null

    init {
        refreshShows()
        query
            .debounce { if (it.isBlank()) 0L else HomeConstants.SEARCH_DEBOUNCE_MS }
            .onEach { search() }
            .launchIn(viewModelScope)
    }

    private val showsState: Flow<HomeUiState> = query
        .map { it.isBlank() }
        .distinctUntilChanged()
        .flatMapLatest { isBlank ->
            if (isBlank) localShowsState() else searchState
        }

    val uiState: StateFlow<HomeUiState> = combine(
        query,
        showsState,
        errorMessageRes
    ) { query, state, error ->
        state.copy(query = query, errorMessageRes = error)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState(isLoading = true))


    fun onQueryChange(newQuery: String) {
        query.value = newQuery
        searchJob?.cancel()
        searchState.value = HomeUiState(isLoading = true)
    }

    fun retry() {
        errorMessageRes.value = null
        if (query.value.isBlank()) refreshShows() else search()
    }

    fun dismissError() {
        errorMessageRes.value = null
    }

    private fun refreshShows() {
        viewModelScope.launch {
            isRefreshing.value = true
            refreshFailed.value = false
            try {
                refreshShowsUseCase(HomeConstants.SHOW_ITEMS_LIMIT)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                refreshFailed.value = true
                errorMessageRes.value = R.string.error_generic_message
            } finally {
                isRefreshing.value = false
            }
        }
    }

    // Cached shows are shown right away; the loading state only applies while the cache is empty.
    private fun localShowsState(): Flow<HomeUiState> = combine(
        getLocalShowsUseCase(),
        isRefreshing,
        refreshFailed
    ) { shows, refreshing, failed ->
        HomeUiState(
            shows = shows,
            isLoading = refreshing && shows.isEmpty(),
            loadFailed = failed && shows.isEmpty()
        )
    }

    private fun search() {
        searchJob?.cancel()
        val query = query.value.trim()
        if (query.isEmpty()) return

        searchJob = viewModelScope.launch {
            searchState.value = HomeUiState(isLoading = true)
            try {
                searchState.value = HomeUiState(shows = searchShowsUseCase(query))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                errorMessageRes.value = R.string.error_generic_message
                searchState.value = HomeUiState(loadFailed = true)
            }
        }
    }
}
