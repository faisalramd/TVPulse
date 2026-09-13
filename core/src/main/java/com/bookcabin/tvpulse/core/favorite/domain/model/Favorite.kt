package com.bookcabin.tvpulse.core.favorite.domain.model

data class Favorite(
    val id: Int,
    val imageUrl: String?,
    val title: String,
    // Comma-separated genres, e.g. "Drama, Horror".
    val genre: String
)
