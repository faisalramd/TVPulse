package com.bookcabin.tvpulse.core.show.data.repository

import com.bookcabin.tvpulse.core.show.data.model.ImageDto
import com.bookcabin.tvpulse.core.show.data.model.RatingDto
import com.bookcabin.tvpulse.core.show.data.model.ShowDto
import com.bookcabin.tvpulse.core.show.data.model.ShowEntity
import com.bookcabin.tvpulse.core.show.data.model.ShowSearchResultDto
import com.bookcabin.tvpulse.core.show.data.source.ShowApi
import com.bookcabin.tvpulse.core.show.data.source.ShowDao
import com.bookcabin.tvpulse.core.show.domain.model.Show
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class ShowRepositoryImplTest {

    private class FakeShowApi : ShowApi {
        var shows: List<ShowDto> = emptyList()
        var searchResults: List<ShowSearchResultDto> = emptyList()
        var detail: ShowDto? = null
        var error: Exception? = null
        val searchQueries = mutableListOf<String>()
        val detailIds = mutableListOf<Int>()

        override suspend fun getShows(): List<ShowDto> {
            error?.let { throw it }
            return shows
        }

        override suspend fun searchShows(query: String): List<ShowSearchResultDto> {
            searchQueries += query
            error?.let { throw it }
            return searchResults
        }

        override suspend fun getShowDetail(id: Int): ShowDto {
            detailIds += id
            error?.let { throw it }
            return checkNotNull(detail)
        }
    }

    private class FakeShowDao : ShowDao {
        val shows = MutableStateFlow<List<ShowEntity>>(emptyList())

        override fun getAllShows(): Flow<List<ShowEntity>> = shows

        override suspend fun insertShows(shows: List<ShowEntity>) {
            this.shows.value = this.shows.value.filterNot { old -> shows.any { it.id == old.id } } + shows
        }

        override suspend fun getShowCount(): Int = shows.value.size
    }

    private val api = FakeShowApi()
    private val dao = FakeShowDao()
    private val repository = ShowRepositoryImpl(api, dao)

    private fun showDto(id: Int) = ShowDto(
        id = id,
        name = "Show $id",
        image = ImageDto(medium = "m$id.jpg", original = "o$id.jpg"),
        rating = RatingDto(average = 8.0),
        genres = listOf("Drama", "Horror"),
        runtime = 60,
        averageRuntime = null,
        status = "Ended",
        summary = "<p>Summary $id</p>"
    )

    @Test
    fun `refreshShows saves only the first N shows`() = runTest {
        api.shows = (1..5).map(::showDto)

        repository.refreshShows(limitItems = 2)

        assertEquals(listOf(1, 2), dao.shows.value.map { it.id })
    }

    @Test
    fun `observeShows emits mapped shows from the dao`() = runTest {
        dao.shows.value = listOf(ShowEntity(id = 1, name = "Under the Dome", imageUrl = "u.jpg", rating = 6.6))

        val shows = repository.observeShows().first()

        assertEquals(listOf(Show(id = 1, name = "Under the Dome", imageUrl = "u.jpg", rating = 6.6)), shows)
    }

    @Test
    fun `refreshShows propagates api errors and leaves the cache untouched`() = runTest {
        dao.shows.value = listOf(ShowEntity(id = 1, name = "Cached", imageUrl = null, rating = null))
        api.error = IOException("offline")

        val result = runCatching { repository.refreshShows(10) }

        assertTrue(result.exceptionOrNull() is IOException)
        assertEquals(listOf(1), dao.shows.value.map { it.id })
    }

    @Test
    fun `searchShows unwraps search results and passes the query`() = runTest {
        api.searchResults = listOf(ShowSearchResultDto(score = 0.9, show = showDto(139)))

        val shows = repository.searchShows("girls")

        assertEquals(listOf("girls"), api.searchQueries)
        assertEquals(listOf(139), shows.map { it.id })
        assertEquals("m139.jpg", shows.single().imageUrl)
    }

    @Test
    fun `searchShows does not write to the cache`() = runTest {
        api.searchResults = listOf(ShowSearchResultDto(score = 0.9, show = showDto(139)))

        repository.searchShows("girls")

        assertTrue(dao.shows.value.isEmpty())
    }

    @Test
    fun `getShowDetail requests the id and maps the detail`() = runTest {
        api.detail = showDto(2993)

        val detail = repository.getShowDetail(2993)

        assertEquals(listOf(2993), api.detailIds)
        assertEquals(2993, detail.id)
        assertEquals("o2993.jpg", detail.imageUrl)
        assertEquals(listOf("Drama", "Horror"), detail.genres)
    }
}
