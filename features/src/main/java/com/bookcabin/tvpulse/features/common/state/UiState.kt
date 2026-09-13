package com.bookcabin.tvpulse.features.common.state

import com.bookcabin.tvpulse.features.common.error.ErrorMessage

sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: ErrorMessage) : UiState<Nothing>()
    data object Empty : UiState<Nothing>()
}
