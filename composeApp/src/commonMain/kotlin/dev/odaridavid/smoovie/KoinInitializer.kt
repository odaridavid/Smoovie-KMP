package dev.odaridavid.smoovie

import dev.odaridavid.smoovie.configuration.ConfigurationModule
import dev.odaridavid.smoovie.movies.MoviesModule
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

fun initKoin() {
    startKoin {
        modules(
            AppModule().module,
            MoviesModule().module,
            ConfigurationModule().module,
        )
    }
}
