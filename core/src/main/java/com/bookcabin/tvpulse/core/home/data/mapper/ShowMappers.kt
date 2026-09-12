package com.bookcabin.tvpulse.core.home.data.mapper

import com.bookcabin.tvpulse.core.home.data.model.ShowDto
import com.bookcabin.tvpulse.core.home.data.model.ShowEntity
import com.bookcabin.tvpulse.core.home.domain.model.Show

fun ShowDto.toDomain() = Show(id = id, name = name)

fun Show.toEntity() = ShowEntity(id = id, name = name)

fun ShowEntity.toDomain() = Show(id = id, name = name)
