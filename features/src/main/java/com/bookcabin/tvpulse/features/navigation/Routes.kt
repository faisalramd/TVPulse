package com.bookcabin.tvpulse.features.navigation

import kotlinx.serialization.Serializable

sealed interface AppRoute

@Serializable
object Home : AppRoute

@Serializable
object Favorite : AppRoute

@Serializable
data class Detail(val showId: Int) : AppRoute


