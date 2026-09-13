package com.bookcabin.tvpulse.core.show.domain.model

data class ShowDetail(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val genres: List<String>,
    val runtimeMinutes: Int?,
    val status: String?,
    val summary: String?,
    val rating: Double?
)
