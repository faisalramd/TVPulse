package com.bookcabin.tvpulse.features.home.presentation.state

sealed interface HomeIntent {
    data object ToggleFirstLaunch : HomeIntent
    data object FetchShows : HomeIntent
}
