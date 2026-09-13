package com.bookcabin.tvpulse.features.favorite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookcabin.tvpulse.core.favorite.domain.usecase.GetFavoritesUseCase
import com.bookcabin.tvpulse.core.favorite.domain.usecase.RemoveFavoriteUseCase
import com.bookcabin.tvpulse.features.favorite.state.FavoriteUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    getFavoritesUseCase: GetFavoritesUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase
) : ViewModel() {

    val uiState: StateFlow<FavoriteUiState> = getFavoritesUseCase()
        .map { favorites -> FavoriteUiState(favorites = favorites) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FavoriteUiState(isLoading = true))

    fun removeFavorite(id: Int) {
        viewModelScope.launch { removeFavoriteUseCase(id) }
    }
}
