package dev.odaridavid.smoovie.movies

import dev.odaridavid.smoovie.configuration.ConfigurationStore
import org.koin.core.annotation.Single

@Single
class MovieUiMapper(
    private val configurationStore: ConfigurationStore,
) {
    fun toUiModels(movies: List<Movie>): List<MovieUiModel> =
        movies.map { movie ->
            movie.toUiModel(configurationStore.backdropUrl(movie.backdropPath))
        }
}
