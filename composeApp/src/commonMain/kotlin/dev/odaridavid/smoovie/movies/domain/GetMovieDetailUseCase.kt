package dev.odaridavid.smoovie.movies.domain

import dev.odaridavid.smoovie.configuration.BackdropSize
import dev.odaridavid.smoovie.configuration.ConfigurationStore
import dev.odaridavid.smoovie.movies.MovieDetailUiModel
import dev.odaridavid.smoovie.movies.toDetailUiModel

class GetMovieDetailUseCase(
    private val repository: MoviesRepository,
    private val configurationStore: ConfigurationStore,
) {
    suspend operator fun invoke(movieId: Int): MovieDetailUiModel =
        repository.getMovieDetail(movieId).let { detail ->
            detail.toDetailUiModel(
                backdropUrl = configurationStore.backdropUrl(detail.backdropPath, BackdropSize.LARGE),
                posterUrl = configurationStore.posterUrl(detail.posterPath),
                profileUrlResolver = { configurationStore.profileUrl(it) },
                movieBackdropUrlResolver = { configurationStore.backdropUrl(it) },
                moviePosterUrlResolver = { configurationStore.posterUrl(it) },
            )
        }
}
