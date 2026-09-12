package com.bookcabin.tvpulse.core.home.domain.usecase

import com.bookcabin.tvpulse.core.home.domain.model.Show
import com.bookcabin.tvpulse.core.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocalShowsUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    operator fun invoke(): Flow<List<Show>> = homeRepository.observeShows()
}
