package com.bookcabin.tvpulse.features.detail.state

import com.bookcabin.tvpulse.core.show.domain.model.ShowDetail

data class DetailUiState(
    val show: ShowDetail? = null,
    val isFavorite: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
