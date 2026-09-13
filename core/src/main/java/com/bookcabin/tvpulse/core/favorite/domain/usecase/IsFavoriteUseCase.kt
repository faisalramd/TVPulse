package com.bookcabin.tvpulse.core.favorite.domain.usecase

import com.bookcabin.tvpulse.core.favorite.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    operator fun invoke(id: Int): Flow<Boolean> = favoriteRepository.observeIsFavorite(id)
}
