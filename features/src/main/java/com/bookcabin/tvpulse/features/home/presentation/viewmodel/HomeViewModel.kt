package com.bookcabin.tvpulse.features.home.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookcabin.tvpulse.core.common.SettingsDataStore
import com.bookcabin.tvpulse.core.home.domain.usecase.GetLocalShowsUseCase
import com.bookcabin.tvpulse.core.home.domain.usecase.RefreshShowsUseCase
import com.bookcabin.tvpulse.features.home.presentation.state.HomeIntent
import com.bookcabin.tvpulse.features.home.presentation.state.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getLocalShowsUseCase: GetLocalShowsUseCase,
    private val refreshShowsUseCase: RefreshShowsUseCase,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        settingsDataStore.isFirstLaunchFlow,
        getLocalShowsUseCase()
    ) { isFirstLaunch, shows ->
        HomeUiState(isFirstLaunch = isFirstLaunch, shows = shows)
    }.stateIn(viewModelScope, SharingStarted.Lazily, HomeUiState())

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.ToggleFirstLaunch -> toggleFirstLaunch()
            HomeIntent.FetchShows -> fetchShows()
        }
    }

    private fun fetchShows() {
        viewModelScope.launch {
            try {
                refreshShowsUseCase()
                Log.d("HomeViewModel", "Successfully refreshed shows.")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching shows", e)
            }
        }
    }

    private fun toggleFirstLaunch() {
        viewModelScope.launch {
            val current = uiState.value.isFirstLaunch
            settingsDataStore.setFirstLaunch(!current)
        }
    }
}
