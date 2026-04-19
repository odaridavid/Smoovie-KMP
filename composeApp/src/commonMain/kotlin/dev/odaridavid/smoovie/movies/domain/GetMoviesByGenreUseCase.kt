package dev.odaridavid.smoovie.movies.domain

import dev.odaridavid.smoovie.movies.MovieUiMapper

class GetMoviesByGenreUseCase(
    private val repository: MoviesRepository,
    private val mapper: MovieUiMapper,
) {
    suspend operator fun invoke(
        genreId: Int,
        page: Int = 1,
    ): MoviesPage {
        val response = repository.discoverMoviesByGenre(genreId, page)
        return MoviesPage(
            movies = mapper.toUiModels(response.results),
            page = response.page,
            totalPages = response.totalPages,
        )
    }
}
