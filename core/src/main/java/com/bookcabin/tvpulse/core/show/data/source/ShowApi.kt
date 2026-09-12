package com.bookcabin.tvpulse.core.show.data.source

import com.bookcabin.tvpulse.core.show.data.model.ShowDto
import retrofit2.http.GET

interface ShowApi {
    @GET("shows")
    suspend fun getShows(): List<ShowDto>
}
