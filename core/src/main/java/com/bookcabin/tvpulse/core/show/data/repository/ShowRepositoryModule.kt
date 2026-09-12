package com.bookcabin.tvpulse.core.show.data.repository

import com.bookcabin.tvpulse.core.show.domain.repository.ShowRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ShowRepositoryModule {

    @Binds
    abstract fun bindHomeRepository(impl: ShowRepositoryImpl): ShowRepository
}
