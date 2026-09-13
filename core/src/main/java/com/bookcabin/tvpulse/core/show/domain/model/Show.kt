package com.bookcabin.tvpulse.core.show.domain.model

data class Show(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val rating: Double?
)
