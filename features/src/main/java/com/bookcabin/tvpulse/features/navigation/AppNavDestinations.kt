package com.bookcabin.tvpulse.features.navigation

import androidx.annotation.StringRes
import com.bookcabin.tvpulse.features.R

enum class AppDestinations(
    @StringRes val labelRes: Int,
    val route: AppRoute
) {
    HOME(R.string.nav_home, Home),
    FAVORITE(R.string.nav_favorite, Favorite)
}
