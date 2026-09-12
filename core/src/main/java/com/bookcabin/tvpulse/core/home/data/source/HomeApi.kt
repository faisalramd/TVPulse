package com.bookcabin.tvpulse.core.home.data.source

import com.bookcabin.tvpulse.core.home.data.model.ShowDto
import retrofit2.http.GET

interface HomeApi {
    @GET("shows")
    suspend fun getShows(): List<ShowDto>
}
