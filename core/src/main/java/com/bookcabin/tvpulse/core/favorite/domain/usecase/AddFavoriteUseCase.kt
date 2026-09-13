package com.bookcabin.tvpulse.core.favorite.domain.usecase

import com.bookcabin.tvpulse.core.favorite.domain.model.Favorite
import com.bookcabin.tvpulse.core.favorite.domain.repository.FavoriteRepository
import javax.inject.Inject

class AddFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(favorite: Favorite) = favoriteRepository.addFavorite(favorite)
}
