package com.bookcabin.tvpulse.data

import retrofit2.http.GET

interface TVMazeApi {
    @GET("shows")
    suspend fun getShows(): List<Show>
}
