package com.bookcabin.tvpulse.core.show.domain.usecase

import com.bookcabin.tvpulse.core.show.domain.model.Show
import com.bookcabin.tvpulse.core.show.domain.repository.ShowRepository
import javax.inject.Inject

class SearchShowsUseCase @Inject constructor(
    private val showRepository: ShowRepository
) {
    suspend operator fun invoke(query: String): List<Show> = showRepository.searchShows(query)
}
