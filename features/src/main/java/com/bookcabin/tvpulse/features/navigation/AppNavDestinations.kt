package com.bookcabin.tvpulse.features.navigation

enum class AppDestinations(
    val label: String,
    val route: AppRoute
) {
    HOME("Home", Home),
    FAVORITE("Favorite", Favorite)
}