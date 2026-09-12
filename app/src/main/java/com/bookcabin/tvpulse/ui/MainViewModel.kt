package com.bookcabin.tvpulse.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookcabin.tvpulse.data.TVMazeApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val tvMazeApi: TVMazeApi
) : ViewModel() {

    fun fetchShows() {
        viewModelScope.launch {
            try {
                val shows = tvMazeApi.getShows()
                Log.d("MainViewModel", "Successfully fetched ${shows.size} shows.")
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error fetching shows", e)
            }
        }
    }
}
