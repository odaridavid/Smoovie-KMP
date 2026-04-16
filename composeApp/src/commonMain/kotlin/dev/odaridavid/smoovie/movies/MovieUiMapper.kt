package dev.odaridavid.smoovie.movies

import dev.odaridavid.smoovie.configuration.ConfigurationStore
import dev.odaridavid.smoovie.movies.data.Movie

class MovieUiMapper(
    private val configurationStore: ConfigurationStore,
) {
    fun toUiModels(movies: List<Movie>): List<MovieUiModel> =
        movies.map { movie ->
            movie.toUiModel(
                backdropUrl = configurationStore.backdropUrl(movie.backdropPath),
                posterUrl = configurationStore.posterUrl(movie.posterPath),
            )
        }
}
