package com.bookcabin.tvpulse.features.detail.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.bookcabin.tvpulse.core.favorite.domain.model.Favorite
import com.bookcabin.tvpulse.core.favorite.domain.usecase.AddFavoriteUseCase
import com.bookcabin.tvpulse.core.favorite.domain.usecase.IsFavoriteUseCase
import com.bookcabin.tvpulse.core.favorite.domain.usecase.RemoveFavoriteUseCase
import com.bookcabin.tvpulse.core.show.domain.model.ShowDetail
import com.bookcabin.tvpulse.core.show.domain.usecase.GetShowDetailUseCase
import com.bookcabin.tvpulse.features.detail.state.DetailUiState
import com.bookcabin.tvpulse.features.navigation.Detail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getShowDetailUseCase: GetShowDetailUseCase,
    isFavoriteUseCase: IsFavoriteUseCase,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase
) : ViewModel() {

    private val showId = savedStateHandle.toRoute<Detail>().showId

    private val _uiState = MutableStateFlow(DetailUiState(isLoading = true))
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadShowDetail()
        isFavoriteUseCase(showId)
            .onEach { isFavorite -> _uiState.update { it.copy(isFavorite = isFavorite) } }
            .launchIn(viewModelScope)
    }

    fun retry() {
        loadShowDetail()
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun toggleFavorite() {
        val state = _uiState.value
        val show = state.show ?: return
        viewModelScope.launch {
            if (state.isFavorite) removeFavoriteUseCase(show.id) else addFavoriteUseCase(show.toFavorite())
        }
    }

    private fun loadShowDetail() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val show = getShowDetailUseCase(showId)
                _uiState.update { it.copy(show = show, isLoading = false) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "terjadi kesalahan") }
            }
        }
    }

    private fun ShowDetail.toFavorite() = Favorite(
        id = id,
        imageUrl = imageUrl,
        title = name,
        genre = genres.joinToString(", ")
    )
}
