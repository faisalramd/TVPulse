package com.bookcabin.tvpulse.core.favorite.data.mapper

import com.bookcabin.tvpulse.core.favorite.data.model.FavoriteEntity
import com.bookcabin.tvpulse.core.favorite.domain.model.Favorite

fun FavoriteEntity.toDomain() = Favorite(id = id, imageUrl = imageUrl, title = title, genre = genre)

fun Favorite.toEntity() = FavoriteEntity(id = id, imageUrl = imageUrl, title = title, genre = genre)
