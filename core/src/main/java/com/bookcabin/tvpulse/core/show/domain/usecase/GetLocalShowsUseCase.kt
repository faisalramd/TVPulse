package com.bookcabin.tvpulse.core.show.domain.usecase

import com.bookcabin.tvpulse.core.show.domain.model.Show
import com.bookcabin.tvpulse.core.show.domain.repository.ShowRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocalShowsUseCase @Inject constructor(
    private val showRepository: ShowRepository
) {
    operator fun invoke(): Flow<List<Show>> = showRepository.observeShows()
}
