package com.bookcabin.tvpulse.features.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bookcabin.tvpulse.core.show.domain.model.Show
import com.bookcabin.tvpulse.features.common.components.EmptyState
import com.bookcabin.tvpulse.features.common.components.ErrorDialog
import com.bookcabin.tvpulse.features.home.state.HomeUiState
import com.bookcabin.tvpulse.features.home.viewmodel.HomeViewModel

private const val PLACEHOLDER_COUNT = 6

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onShowClick: (Show) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        ShowSearchBar(
            query = uiState.query,
            onQueryChange = viewModel::onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp)
        ) {
            Text(
                text = "Daftar Acara Populer".uppercase(),
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "(30 Film)",
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        ShowGridContent(
            uiState = uiState,
            onShowClick = onShowClick,
            onRetry = viewModel::retry
        )
    }

    uiState.errorMessage?.let { message ->
        ErrorDialog(
            message = message,
            onRetry = viewModel::retry,
            onDismiss = viewModel::dismissError
        )
    }
}

@Composable
private fun ShowSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Cari serial TV (misal: horror)...") },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Filled.Clear, contentDescription = "Clear search")
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(28.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
    )
}

@Composable
private fun ShowGridContent(
    uiState: HomeUiState,
    onShowClick: (Show) -> Unit,
    onRetry: () -> Unit
) {
    when {
        uiState.loadFailed -> EmptyState(message = "Couldn't load shows.", onRetry = onRetry)

        !uiState.isLoading && uiState.shows.isEmpty() -> EmptyState(
            message = if (uiState.query.isBlank()) "No shows yet." else "No shows found for \"${uiState.query}\"."
        )

        else -> LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (uiState.isLoading) {
                items(PLACEHOLDER_COUNT) { ShowCardPlaceholder() }
            } else {
                items(uiState.shows, key = { it.id }) { show ->
                    ShowCard(show = show, onClick = { onShowClick(show) })
                }
            }
        }
    }
}
