package com.bookcabin.tvpulse.features.common.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.bookcabin.tvpulse.features.R

@Composable
fun ErrorDialog(message: String, onRetry: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.error_dialog_title)) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onRetry) { Text(stringResource(R.string.error_dialog_retry)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.error_dialog_dismiss)) }
        }
    )
}
