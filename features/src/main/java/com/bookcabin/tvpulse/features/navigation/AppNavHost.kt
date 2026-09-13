package com.bookcabin.tvpulse.features.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.bookcabin.tvpulse.features.home.presentation.ui.HomeScreen
import com.bookcabin.tvpulse.features.home.presentation.viewmodel.HomeViewModel

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Home,
        modifier = modifier
    ) {
        composable<Home>(
            deepLinks = listOf(navDeepLink<Home>(basePath = DeepLinks.HOME))
        ) {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(viewModel = viewModel)
        }
        composable<Favorite>(
            deepLinks = listOf(navDeepLink<Favorite>(basePath = DeepLinks.FAVORITE))
        ) {
            DummyScreen(title = "Favorite Screen")
        }
    }
}

@Composable
fun DummyScreen(title: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = title)
    }
}
