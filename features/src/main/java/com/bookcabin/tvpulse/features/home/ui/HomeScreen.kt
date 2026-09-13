package com.bookcabin.tvpulse.features.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bookcabin.tvpulse.core.show.domain.model.Show
import com.bookcabin.tvpulse.features.R
import com.bookcabin.tvpulse.features.common.components.EmptyState
import com.bookcabin.tvpulse.features.common.components.ErrorDialog
import com.bookcabin.tvpulse.features.common.error.asString
import com.bookcabin.tvpulse.features.common.state.UiState
import com.bookcabin.tvpulse.features.home.constant.HomeConstants
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
                text = if (uiState.query.isBlank()) {
                    stringResource(R.string.home_title_popular).uppercase()
                } else {
                    stringResource(R.string.home_title_search)
                },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = stringResource(R.string.film_count, HomeConstants.SHOW_ITEMS_LIMIT),
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

    uiState.errorMessage?.let { errorMessage ->
        ErrorDialog(
            message = errorMessage.asString(),
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
        placeholder = { Text(stringResource(R.string.home_search_placeholder)) },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Filled.Clear, contentDescription = stringResource(R.string.home_search_clear))
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
    when (val showsState = uiState.showsState) {
        UiState.Loading -> ShowGrid {
            items(PLACEHOLDER_COUNT) { ShowCardPlaceholder() }
        }

        is UiState.Success -> ShowGrid {
            items(showsState.data, key = { it.id }) { show ->
                ShowCard(show = show, onClick = { onShowClick(show) })
            }
        }

        is UiState.Error -> EmptyState(message = stringResource(R.string.home_load_failed), onRetry = onRetry)

        UiState.Empty -> if (uiState.query.isBlank()) {
            EmptyState(message = stringResource(R.string.home_empty))
        } else {
            SearchNotFound(query = uiState.query)
        }
    }
}

@Composable
private fun ShowGrid(content: LazyGridScope.() -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content
    )
}
