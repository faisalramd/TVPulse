package com.bookcabin.tvpulse.features.favorite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookcabin.tvpulse.core.favorite.domain.model.Favorite
import com.bookcabin.tvpulse.core.favorite.domain.usecase.GetFavoritesUseCase
import com.bookcabin.tvpulse.core.favorite.domain.usecase.RemoveFavoriteUseCase
import com.bookcabin.tvpulse.features.common.error.ErrorMessageMapper
import com.bookcabin.tvpulse.features.common.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    getFavoritesUseCase: GetFavoritesUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase
) : ViewModel() {

    val uiState: StateFlow<UiState<List<Favorite>>> = getFavoritesUseCase()
        .map { favorites ->
            if (favorites.isEmpty()) UiState.Empty else UiState.Success(favorites)
        }
        .catch { emit(UiState.Error(ErrorMessageMapper.map(it))) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    fun removeFavorite(id: Int) {
        viewModelScope.launch { removeFavoriteUseCase(id) }
    }
}
