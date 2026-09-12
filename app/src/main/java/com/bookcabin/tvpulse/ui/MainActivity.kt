package com.bookcabin.tvpulse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.bookcabin.tvpulse.ui.MainViewModel
import com.bookcabin.tvpulse.ui.theme.TVPulseTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            
            TVPulseTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DebugScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun DebugScreen(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val isFirstLaunch by viewModel.isFirstLaunch.collectAsState()
    val localShows by viewModel.localShows.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Debug UI")

        // 1. DataStore Toggle
        Button(onClick = { viewModel.toggleFirstLaunch() }) {
            Text(text = "Is First Launch (DataStore): $isFirstLaunch")
        }

        // 2. Room Database Fetch
        Button(onClick = { viewModel.fetchShows() }) {
            Text("Fetch API & Save to DB")
        }
        Text("Local Shows Count (Room): ${localShows.size}")

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