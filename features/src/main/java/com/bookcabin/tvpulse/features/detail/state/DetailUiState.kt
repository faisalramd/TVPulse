package com.bookcabin.tvpulse.features.detail.state

import com.bookcabin.tvpulse.core.show.domain.model.ShowDetail
import com.bookcabin.tvpulse.features.common.error.ErrorMessage
import com.bookcabin.tvpulse.features.common.state.UiState

data class DetailUiState(
    val detailState: UiState<ShowDetail> = UiState.Loading,
    val isFavorite: Boolean = false,
    val errorMessage: ErrorMessage? = null
)
