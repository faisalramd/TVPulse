package com.bookcabin.tvpulse.core.favorite.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val id: Int,
    val imageUrl: String?,
    val title: String,
    val genre: String
)
