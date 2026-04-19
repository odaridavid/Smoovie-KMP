package dev.odaridavid.smoovie.movies.domain

import dev.odaridavid.smoovie.movies.MovieUiMapper

class GetPopularMoviesUseCase(
    private val repository: MoviesRepository,
    private val mapper: MovieUiMapper,
) {
    suspend operator fun invoke(page: Int = 1): MoviesPage {
        val response = repository.getPopularMovies(page)
        return MoviesPage(
            movies = mapper.toUiModels(response.results),
            page = response.page,
            totalPages = response.totalPages,
        )
    }
}
