package com.bookcabin.tvpulse.core.show.data.mapper

import com.bookcabin.tvpulse.core.show.data.model.ShowDto
import com.bookcabin.tvpulse.core.show.data.model.ShowEntity
import com.bookcabin.tvpulse.core.show.domain.model.Show

fun ShowDto.toDomain() = Show(
    id = id,
    name = name,
    imageUrl = image?.medium ?: image?.original,
    rating = rating?.average
)

fun Show.toEntity() = ShowEntity(id = id, name = name, imageUrl = imageUrl, rating = rating)

fun ShowEntity.toDomain() = Show(id = id, name = name, imageUrl = imageUrl, rating = rating)

fun List<ShowDto>.toDomain(limitItems: Int?) = if (limitItems != null) {
    take(limitItems)
} else {
    this
}.map { it.toDomain() }

