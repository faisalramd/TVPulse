package com.bookcabin.tvpulse.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookcabin.tvpulse.data.TVMazeApi
import com.bookcabin.tvpulse.data.local.SettingsDataStore
import com.bookcabin.tvpulse.data.local.ShowDao
import com.bookcabin.tvpulse.data.local.toEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val tvMazeApi: TVMazeApi,
    private val showDao: ShowDao,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    val isFirstLaunch: StateFlow<Boolean> = settingsDataStore.isFirstLaunchFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    val localShows = showDao.getAllShows()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun fetchShows() {
        viewModelScope.launch {
            try {
                val shows = tvMazeApi.getShows()
                Log.d("MainViewModel", "Successfully fetched ${shows.size} shows.")
                showDao.insertShows(shows.map { it.toEntity() })
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error fetching shows", e)
            }
        }
    }

    fun toggleFirstLaunch() {
        viewModelScope.launch {
            val current = isFirstLaunch.value
            settingsDataStore.setFirstLaunch(!current)
        }
    }
}
