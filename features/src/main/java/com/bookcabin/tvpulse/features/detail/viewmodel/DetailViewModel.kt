package com.bookcabin.tvpulse.features.detail.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.bookcabin.tvpulse.features.detail.state.DetailUiState
import com.bookcabin.tvpulse.features.navigation.Detail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val route = savedStateHandle.toRoute<Detail>()

    private val _uiState = MutableStateFlow(DetailUiState(showId = route.showId))
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()
}
