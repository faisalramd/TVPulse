package com.bookcabin.tvpulse.features.testutil

import com.bookcabin.tvpulse.core.show.domain.model.Show
import com.bookcabin.tvpulse.core.show.domain.model.ShowDetail
import com.bookcabin.tvpulse.core.show.domain.repository.ShowRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeShowRepository : ShowRepository {

    val localShows = MutableStateFlow<List<Show>>(emptyList())

    // Each behaviour can be swapped per test, e.g. to throw an exception.
    var refreshResult: suspend (limitItems: Int?) -> List<Show> = { emptyList() }
    var searchResult: suspend (query: String) -> List<Show> = { emptyList() }
    var detailResult: suspend (id: Int) -> ShowDetail = { error("detailResult not set") }

    val refreshLimits = mutableListOf<Int?>()
    val searchQueries = mutableListOf<String>()
    val requestedDetailIds = mutableListOf<Int>()

    override fun observeShows(): Flow<List<Show>> = localShows

    override suspend fun refreshShows(limitItems: Int?) {
        refreshLimits += limitItems
        localShows.value = refreshResult(limitItems)
    }

    override suspend fun searchShows(query: String): List<Show> {
        searchQueries += query
        return searchResult(query)
    }

    override suspend fun getShowDetail(id: Int): ShowDetail {
        requestedDetailIds += id
        return detailResult(id)
    }
}
