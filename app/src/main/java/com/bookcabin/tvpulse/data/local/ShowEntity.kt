package com.bookcabin.tvpulse.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bookcabin.tvpulse.data.Show

@Entity(tableName = "shows")
data class ShowEntity(
    @PrimaryKey
    val id: Int,
    val name: String
)

fun Show.toEntity() = ShowEntity(
    id = id,
    name = name
)
