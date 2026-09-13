package com.bookcabin.tvpulse.features.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.bookcabin.tvpulse.features.detail.ui.DetailScreen
import com.bookcabin.tvpulse.features.detail.viewmodel.DetailViewModel
import com.bookcabin.tvpulse.features.favorite.ui.FavoriteScreen
import com.bookcabin.tvpulse.features.favorite.viewmodel.FavoriteViewModel
import com.bookcabin.tvpulse.features.home.ui.HomeScreen
import com.bookcabin.tvpulse.features.home.viewmodel.HomeViewModel

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
            HomeScreen(
                viewModel = viewModel,
                onShowClick = { show -> navController.navigate(Detail(showId = show.id)) }
            )
        }
        composable<Favorite>(
            deepLinks = listOf(navDeepLink<Favorite>(basePath = DeepLinks.FAVORITE))
        ) {
            val viewModel: FavoriteViewModel = hiltViewModel()
            FavoriteScreen(viewModel = viewModel)
        }
        composable<Detail> {
            val viewModel: DetailViewModel = hiltViewModel()
            DetailScreen(viewModel = viewModel)
        }
    }
}
