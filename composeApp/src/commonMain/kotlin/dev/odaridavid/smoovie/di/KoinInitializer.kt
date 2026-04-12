package dev.odaridavid.smoovie.di

import dev.odaridavid.smoovie.ui.movies.movieModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(
            appModule,
            movieModule,
        )
    }
}
