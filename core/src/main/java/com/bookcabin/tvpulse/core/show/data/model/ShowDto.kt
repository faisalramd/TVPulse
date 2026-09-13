package com.bookcabin.tvpulse.core.show.data.model

data class ShowDto(
    val id: Int,
    val name: String,
    val image: ImageDto?,
    val rating: RatingDto?
)

data class ImageDto(
    val medium: String?,
    val original: String?
)

data class RatingDto(
    val average: Double?
)

data class ShowSearchResultDto(
    val score: Double,
    val show: ShowDto
)
