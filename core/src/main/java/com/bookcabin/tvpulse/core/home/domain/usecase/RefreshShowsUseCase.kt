package com.bookcabin.tvpulse.core.home.domain.usecase

import com.bookcabin.tvpulse.core.home.domain.repository.HomeRepository
import javax.inject.Inject

class RefreshShowsUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    suspend operator fun invoke() = homeRepository.refreshShows()
}
