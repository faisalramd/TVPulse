package com.bookcabin.tvpulse.core.favorite.data.mapper

import com.bookcabin.tvpulse.core.favorite.data.model.FavoriteEntity
import com.bookcabin.tvpulse.core.favorite.domain.model.Favorite
import org.junit.Assert.assertEquals
import org.junit.Test

class FavoriteMappersTest {

    @Test
    fun `Favorite and FavoriteEntity round-trip without losing data`() {
        val favorite = Favorite(id = 2993, imageUrl = "st.jpg", title = "Stranger Things", genre = "Drama, Horror")

        val entity = favorite.toEntity()

        assertEquals(FavoriteEntity(id = 2993, imageUrl = "st.jpg", title = "Stranger Things", genre = "Drama, Horror"), entity)
        assertEquals(favorite, entity.toDomain())
    }

    @Test
    fun `missing image stays null`() {
        val favorite = Favorite(id = 1, imageUrl = null, title = "No Image", genre = "")

        assertEquals(favorite, favorite.toEntity().toDomain())
    }
}
