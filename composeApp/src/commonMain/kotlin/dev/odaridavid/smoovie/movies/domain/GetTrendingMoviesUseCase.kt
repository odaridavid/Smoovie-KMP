package dev.odaridavid.smoovie.movies.domain

import dev.odaridavid.smoovie.movies.MovieUiMapper
import dev.odaridavid.smoovie.movies.MovieUiModel

class GetTrendingMoviesUseCase(
    private val repository: MoviesRepository,
    private val mapper: MovieUiMapper,
) {
    suspend operator fun invoke(): List<MovieUiModel> = mapper.toUiModels(repository.getTrendingMovies().results)
}
