package com.bookcabin.tvpulse.features.home.state

import androidx.annotation.StringRes
import com.bookcabin.tvpulse.core.show.domain.model.Show

data class HomeUiState(
    val query: String = "",
    val shows: List<Show> = emptyList(),
    val isLoading: Boolean = false,
    val loadFailed: Boolean = false,
    @StringRes val errorMessageRes: Int? = null
)
