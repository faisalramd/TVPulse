package com.bookcabin.tvpulse.core.show.domain.usecase

import com.bookcabin.tvpulse.core.show.domain.model.ShowDetail
import com.bookcabin.tvpulse.core.show.domain.repository.ShowRepository
import javax.inject.Inject

class GetShowDetailUseCase @Inject constructor(
    private val showRepository: ShowRepository
) {
    suspend operator fun invoke(id: Int): ShowDetail = showRepository.getShowDetail(id)
}
