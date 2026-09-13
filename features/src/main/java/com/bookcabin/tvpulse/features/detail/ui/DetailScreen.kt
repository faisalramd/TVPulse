package com.bookcabin.tvpulse.features.detail.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bookcabin.tvpulse.core.show.domain.model.ShowDetail
import com.bookcabin.tvpulse.features.R
import com.bookcabin.tvpulse.features.common.components.ErrorDialog
import com.bookcabin.tvpulse.features.common.error.asString
import com.bookcabin.tvpulse.features.common.components.shimmer
import com.bookcabin.tvpulse.features.detail.viewmodel.DetailViewModel

private const val IMAGE_ASPECT_RATIO = 16f / 10f
private val ImageShape = RoundedCornerShape(16.dp)

@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        IconButton(onClick = onBackClick, modifier = Modifier.padding(4.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.detail_back))
        }

        val show = uiState.show
        when {
            uiState.isLoading -> DetailPlaceholder()
            show != null -> DetailContent(
                show = show,
                isFavorite = uiState.isFavorite,
                onFavoriteClick = viewModel::toggleFavorite
            )
        }
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
private fun DetailContent(show: ShowDetail, isFavorite: Boolean, onFavoriteClick: () -> Unit) {
    Column(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = show.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        AsyncImage(
            model = show.imageUrl,
            contentDescription = show.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(IMAGE_ASPECT_RATIO)
                .clip(ImageShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )

        val runtimeLabel = show.runtimeMinutes?.let { stringResource(R.string.detail_runtime_minutes, it) }
        val statusLabel = show.status?.let { status ->
            statusLabelRes(status)?.let { stringResource(it) } ?: status
        }
        val tags = show.genres + listOfNotNull(runtimeLabel, statusLabel)
        if (tags.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tags.forEach { TagChip(text = it) }
            }
        }

        if (isFavorite) {
            OutlinedButton(onClick = onFavoriteClick, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.detail_remove_favorite))
            }
        } else {
            FilledTonalButton(onClick = onFavoriteClick, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.detail_add_favorite))
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.detail_synopsis),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = show.summary?.let { AnnotatedString.fromHtml(it.trim()) }
                    ?: AnnotatedString(stringResource(R.string.detail_synopsis_unavailable)),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun TagChip(text: String) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun DetailPlaceholder() {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(32.dp)
                .shimmer(cornerRadius = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(IMAGE_ASPECT_RATIO)
                .shimmer(cornerRadius = 16.dp)
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(4) {
                Box(
                    modifier = Modifier
                        .width(64.dp)
                        .height(28.dp)
                        .shimmer(cornerRadius = 14.dp)
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .shimmer(cornerRadius = 20.dp)
        )
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(24.dp)
                .shimmer(cornerRadius = 8.dp)
        )
        repeat(5) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .shimmer(cornerRadius = 4.dp)
            )
        }
    }
}

// Maps TVMaze status values to a localized label; null for unknown statuses, which are shown as-is.
@StringRes
private fun statusLabelRes(status: String): Int? = when (status) {
    "Running" -> R.string.detail_status_running
    "Ended" -> R.string.detail_status_ended
    "To Be Determined" -> R.string.detail_status_to_be_determined
    "In Development" -> R.string.detail_status_in_development
    else -> null
}
