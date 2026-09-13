package com.bookcabin.tvpulse.features.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookcabin.tvpulse.core.show.domain.model.Show
import com.bookcabin.tvpulse.core.show.domain.usecase.GetLocalShowsUseCase
import com.bookcabin.tvpulse.core.show.domain.usecase.RefreshShowsUseCase
import com.bookcabin.tvpulse.core.show.domain.usecase.SearchShowsUseCase
import com.bookcabin.tvpulse.features.common.error.ErrorMessage
import com.bookcabin.tvpulse.features.common.error.ErrorMessageMapper
import com.bookcabin.tvpulse.features.common.state.UiState
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
    private val isRefreshing = MutableStateFlow(true)
    private val refreshError = MutableStateFlow<ErrorMessage?>(null)
    private val searchState = MutableStateFlow<UiState<List<Show>>>(UiState.Loading)
    private val errorMessage = MutableStateFlow<ErrorMessage?>(null)
    private var searchJob: Job? = null

    init {
        refreshShows()
        query
            .debounce { if (it.isBlank()) 0L else HomeConstants.SEARCH_DEBOUNCE_MS }
            .onEach { search() }
            .launchIn(viewModelScope)
    }

    private val showsState: Flow<UiState<List<Show>>> = query
        .map { it.isBlank() }
        .distinctUntilChanged()
        .flatMapLatest { isBlank ->
            if (isBlank) localShowsState() else searchState
        }

    val uiState: StateFlow<HomeUiState> = combine(
        query,
        showsState,
        errorMessage
    ) { query, showsState, error ->
        HomeUiState(query = query, showsState = showsState, errorMessage = error)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun onQueryChange(newQuery: String) {
        query.value = newQuery
        searchJob?.cancel()
        searchState.value = UiState.Loading
    }

    fun retry() {
        errorMessage.value = null
        if (query.value.isBlank()) refreshShows() else search()
    }

    fun dismissError() {
        errorMessage.value = null
    }

    private fun refreshShows() {
        viewModelScope.launch {
            isRefreshing.value = true
            refreshError.value = null
            try {
                refreshShowsUseCase(HomeConstants.SHOW_ITEMS_LIMIT)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val message = ErrorMessageMapper.map(e)
                refreshError.value = message
                errorMessage.value = message
            } finally {
                isRefreshing.value = false
            }
        }
    }

    private fun localShowsState(): Flow<UiState<List<Show>>> = combine(
        getLocalShowsUseCase(),
        isRefreshing,
        refreshError
    ) { shows, refreshing, error ->
        when {
            shows.isNotEmpty() -> UiState.Success(shows)
            refreshing -> UiState.Loading
            error != null -> UiState.Error(error)
            else -> UiState.Empty
        }
    }

    private fun search() {
        searchJob?.cancel()
        val query = query.value.trim()
        if (query.isEmpty()) return

        searchJob = viewModelScope.launch {
            searchState.value = UiState.Loading
            try {
                val shows = searchShowsUseCase(query)
                searchState.value = if (shows.isEmpty()) UiState.Empty else UiState.Success(shows)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val message = ErrorMessageMapper.map(e)
                errorMessage.value = message
                searchState.value = UiState.Error(message)
            }
        }
    }
}
