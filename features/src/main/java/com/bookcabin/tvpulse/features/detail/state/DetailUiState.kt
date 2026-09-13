package com.bookcabin.tvpulse.features.detail.state

import com.bookcabin.tvpulse.core.show.domain.model.ShowDetail

data class DetailUiState(
    val show: ShowDetail? = null,
    val isLoading: Boolean = false,
    // Shown in an error dialog until dismissed or retried.
    val errorMessage: String? = null
)
