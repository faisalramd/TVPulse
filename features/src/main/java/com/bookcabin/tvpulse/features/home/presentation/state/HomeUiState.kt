package com.bookcabin.tvpulse.features.home.presentation.state

import com.bookcabin.tvpulse.core.show.domain.model.Show

data class HomeUiState(
    val isFirstLaunch: Boolean = true,
    val shows: List<Show> = emptyList()
)
