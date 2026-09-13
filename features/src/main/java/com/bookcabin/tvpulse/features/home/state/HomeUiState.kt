package com.bookcabin.tvpulse.features.home.state

import com.bookcabin.tvpulse.core.show.domain.model.Show
import com.bookcabin.tvpulse.features.common.error.ErrorMessage
import com.bookcabin.tvpulse.features.common.state.UiState

data class HomeUiState(
    val query: String = "",
    val showsState: UiState<List<Show>> = UiState.Loading,
    val errorMessage: ErrorMessage? = null
)
