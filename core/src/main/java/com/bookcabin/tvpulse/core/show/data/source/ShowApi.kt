package com.bookcabin.tvpulse.core.show.data.source

import com.bookcabin.tvpulse.core.show.data.model.ShowDto
import com.bookcabin.tvpulse.core.show.data.model.ShowSearchResultDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ShowApi {
    @GET("shows")
    suspend fun getShows(): List<ShowDto>

    @GET("search/shows")
    suspend fun searchShows(@Query("q") query: String): List<ShowSearchResultDto>
}
