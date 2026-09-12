package com.bookcabin.tvpulse.core.show.domain.usecase

import com.bookcabin.tvpulse.core.show.domain.repository.ShowRepository
import javax.inject.Inject

class RefreshShowsUseCase @Inject constructor(
    private val showRepository: ShowRepository
) {
    suspend operator fun invoke() = showRepository.refreshShows()
}
