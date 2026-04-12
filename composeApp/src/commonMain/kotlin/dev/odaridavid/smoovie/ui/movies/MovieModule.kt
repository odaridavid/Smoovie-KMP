package dev.odaridavid.smoovie.ui.movies

import dev.odaridavid.smoovie.data.MoviesRepository
import dev.odaridavid.smoovie.data.MoviesRepositoryImpl
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val movieModule =
    module {
        single { MoviesRepositoryImpl(get()) } bind MoviesRepository::class

        viewModelOf(::MoviesViewModel)
    }
