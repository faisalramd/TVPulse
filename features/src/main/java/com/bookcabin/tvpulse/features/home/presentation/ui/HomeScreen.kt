package com.bookcabin.tvpulse.features.home.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.bookcabin.tvpulse.features.home.presentation.state.HomeIntent
import com.bookcabin.tvpulse.features.home.presentation.viewmodel.HomeViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel, modifier: Modifier = Modifier) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Debug UI")

        // 1. DataStore Toggle
        Button(onClick = { viewModel.onIntent(HomeIntent.ToggleFirstLaunch) }) {
            Text(text = "Is First Launch (DataStore): ${uiState.isFirstLaunch}")
        }

        // 2. Room Database Fetch
        Button(onClick = { viewModel.onIntent(HomeIntent.FetchShows) }) {
            Text("Fetch API & Save to DB")
        }
        Text("Local Shows Count (Room): ${uiState.shows.size}")

        // 3. Coil Image
        AsyncImage(
            model = "https://static.tvmaze.com/uploads/images/medium_portrait/81/202627.jpg",
            contentDescription = "Test Coil Image",
            modifier = Modifier.size(150.dp)
        )

        // 4. Lottie Animation
        val composition by rememberLottieComposition(LottieCompositionSpec.Url("https://lottie.host/7d83a613-8246-4f52-aca5-166a825c706a/PJu2OabbfS.lottie"))
        LottieAnimation(
            composition = composition,
            iterations = 1,
            modifier = Modifier.size(100.dp)
        )
    }
}
