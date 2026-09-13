package com.bookcabin.tvpulse.features.common.error

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

// A user-facing error message, resolved to text in the UI so ViewModels don't need a Context.
data class ErrorMessage(
    @StringRes val resId: Int,
    val formatArgs: List<Any> = emptyList()
)

@Composable
fun ErrorMessage.asString(): String = stringResource(resId, *formatArgs.toTypedArray())
