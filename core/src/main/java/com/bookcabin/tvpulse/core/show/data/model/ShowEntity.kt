package com.bookcabin.tvpulse.core.show.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shows")
data class ShowEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val rating: Double?
)
