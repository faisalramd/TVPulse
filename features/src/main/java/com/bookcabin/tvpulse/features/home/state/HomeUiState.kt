package com.bookcabin.tvpulse.features.home.state

import com.bookcabin.tvpulse.core.show.domain.model.Show
import com.bookcabin.tvpulse.features.common.error.ErrorMessage

data class HomeUiState(
    val query: String = "",
    val shows: List<Show> = emptyList(),
    val isLoading: Boolean = false,
    val loadFailed: Boolean = false,
    val errorMessage: ErrorMessage? = null
)
