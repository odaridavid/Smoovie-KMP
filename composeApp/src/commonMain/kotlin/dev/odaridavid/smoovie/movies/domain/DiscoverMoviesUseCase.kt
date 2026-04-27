package dev.odaridavid.smoovie.movies.domain

import dev.odaridavid.smoovie.filter.MovieFilterPreferences
import dev.odaridavid.smoovie.movies.MovieUiMapper

class DiscoverMoviesUseCase(
    private val repository: MoviesRepository,
    private val mapper: MovieUiMapper,
) {
    suspend operator fun invoke(
        filter: MovieFilterPreferences,
        page: Int = 1,
    ): MoviesPage {
        val response =
            repository.discoverMovies(
                genreId = filter.selectedGenreId,
                sortBy = filter.sortBy.apiValue,
                minRating = filter.minRating,
                page = page,
            )
        return MoviesPage(
            movies = mapper.toUiModels(response.results),
            page = response.page,
            totalPages = response.totalPages,
        )
    }
}
