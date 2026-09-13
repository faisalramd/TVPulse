package com.bookcabin.tvpulse.features.favorite.state

import com.bookcabin.tvpulse.core.favorite.domain.model.Favorite

data class FavoriteUiState(
    val favorites: List<Favorite> = emptyList(),
    val isLoading: Boolean = false
)
