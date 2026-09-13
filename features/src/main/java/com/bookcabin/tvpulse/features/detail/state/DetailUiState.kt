package com.bookcabin.tvpulse.features.detail.state

import com.bookcabin.tvpulse.core.show.domain.model.ShowDetail
import com.bookcabin.tvpulse.features.common.error.ErrorMessage

data class DetailUiState(
    val show: ShowDetail? = null,
    val isFavorite: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: ErrorMessage? = null
)
