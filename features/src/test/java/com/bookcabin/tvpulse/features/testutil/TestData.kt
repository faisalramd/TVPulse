package com.bookcabin.tvpulse.features.testutil

import com.bookcabin.tvpulse.core.favorite.domain.model.Favorite
import com.bookcabin.tvpulse.core.show.domain.model.Show
import com.bookcabin.tvpulse.core.show.domain.model.ShowDetail
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

object TestData {

    val underTheDome = Show(id = 1, name = "Under the Dome", imageUrl = "https://img/1.jpg", rating = 6.6)
    val personOfInterest = Show(id = 2, name = "Person of Interest", imageUrl = "https://img/2.jpg", rating = 8.8)
    val girls = Show(id = 139, name = "Girls", imageUrl = "https://img/139.jpg", rating = 6.5)

    val strangerThings = ShowDetail(
        id = 2993,
        name = "Stranger Things",
        imageUrl = "https://img/2993.jpg",
        genres = listOf("Drama", "Horror"),
        runtimeMinutes = 65,
        status = "Ended",
        summary = "<p>A young boy vanishes.</p>",
        rating = 8.6
    )

    val strangerThingsFavorite = Favorite(
        id = 2993,
        imageUrl = "https://img/2993.jpg",
        title = "Stranger Things",
        genre = "Drama, Horror"
    )

    fun httpException(code: Int) = HttpException(Response.error<Any>(code, "".toResponseBody()))
}
