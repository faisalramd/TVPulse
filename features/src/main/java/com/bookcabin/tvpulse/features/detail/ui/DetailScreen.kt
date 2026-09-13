package com.bookcabin.tvpulse.features.detail.ui

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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bookcabin.tvpulse.core.show.domain.model.ShowDetail
import com.bookcabin.tvpulse.features.common.components.ErrorDialog
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
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
        }

        val show = uiState.show
        when {
            uiState.isLoading -> DetailPlaceholder()
            show != null -> DetailContent(show = show)
        }
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
private fun DetailContent(show: ShowDetail) {
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

        val tags = show.genres +
            listOfNotNull(show.runtimeMinutes?.let { "$it min" }, show.status?.toStatusLabel())
        if (tags.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tags.forEach { TagChip(text = it) }
            }
        }

        // Favorites aren't implemented yet.
        FilledTonalButton(onClick = {}, modifier = Modifier.fillMaxWidth()) {
            Text("TAMBAH KE FAVORIT")
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Sinopsis",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = show.summary?.let { AnnotatedString.fromHtml(it.trim()) }
                    ?: AnnotatedString("Sinopsis belum tersedia."),
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

private fun String.toStatusLabel(): String = when (this) {
    "Running" -> "Berjalan"
    "Ended" -> "Selesai"
    "To Be Determined" -> "Belum Pasti"
    "In Development" -> "Dalam Pengembangan"
    else -> this
}
