package com.bookcabin.tvpulse.core.show.data.mapper

import com.bookcabin.tvpulse.core.show.data.model.ImageDto
import com.bookcabin.tvpulse.core.show.data.model.RatingDto
import com.bookcabin.tvpulse.core.show.data.model.ShowDto
import com.bookcabin.tvpulse.core.show.data.model.ShowEntity
import com.bookcabin.tvpulse.core.show.domain.model.Show
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ShowMappersTest {

    private fun showDto(
        id: Int = 1,
        image: ImageDto? = ImageDto(medium = "medium.jpg", original = "original.jpg"),
        rating: RatingDto? = RatingDto(average = 7.5),
        genres: List<String>? = listOf("Drama"),
        runtime: Int? = 60,
        averageRuntime: Int? = 55
    ) = ShowDto(
        id = id,
        name = "Show $id",
        image = image,
        rating = rating,
        genres = genres,
        runtime = runtime,
        averageRuntime = averageRuntime,
        status = "Running",
        summary = "<p>Summary</p>"
    )

    @Test
    fun `ShowDto to Show prefers the medium image`() {
        val show = showDto().toDomain()

        assertEquals(Show(id = 1, name = "Show 1", imageUrl = "medium.jpg", rating = 7.5), show)
    }

    @Test
    fun `ShowDto to Show falls back to the original image`() {
        val show = showDto(image = ImageDto(medium = null, original = "original.jpg")).toDomain()

        assertEquals("original.jpg", show.imageUrl)
    }

    @Test
    fun `ShowDto to Show keeps missing image and rating as null`() {
        val show = showDto(image = null, rating = null).toDomain()

        assertNull(show.imageUrl)
        assertNull(show.rating)
    }

    @Test
    fun `list toDomain applies the limit`() {
        val dtos = (1..5).map { showDto(id = it) }

        assertEquals(listOf(1, 2, 3), dtos.toDomain(limitItems = 3).map { it.id })
    }

    @Test
    fun `list toDomain without a limit keeps every item`() {
        val dtos = (1..5).map { showDto(id = it) }

        assertEquals(5, dtos.toDomain(limitItems = null).size)
    }

    @Test
    fun `ShowDto to ShowDetail prefers the original image and runtime`() {
        val detail = showDto().toShowDetail()

        assertEquals("original.jpg", detail.imageUrl)
        assertEquals(60, detail.runtimeMinutes)
        assertEquals(listOf("Drama"), detail.genres)
        assertEquals("Running", detail.status)
        assertEquals("<p>Summary</p>", detail.summary)
        assertEquals(7.5, detail.rating!!, 0.0)
    }

    @Test
    fun `ShowDto to ShowDetail falls back to medium image and average runtime`() {
        val detail = showDto(
            image = ImageDto(medium = "medium.jpg", original = null),
            runtime = null,
            averageRuntime = 55
        ).toShowDetail()

        assertEquals("medium.jpg", detail.imageUrl)
        assertEquals(55, detail.runtimeMinutes)
    }

    @Test
    fun `ShowDto to ShowDetail turns missing genres into an empty list`() {
        val detail = showDto(genres = null).toShowDetail()

        assertEquals(emptyList<String>(), detail.genres)
    }

    @Test
    fun `Show and ShowEntity round-trip without losing data`() {
        val show = Show(id = 7, name = "Arrow", imageUrl = "arrow.jpg", rating = 7.4)

        val entity = show.toEntity()

        assertEquals(ShowEntity(id = 7, name = "Arrow", imageUrl = "arrow.jpg", rating = 7.4), entity)
        assertEquals(show, entity.toDomain())
    }
}
