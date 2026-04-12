package dev.odaridavid.smoovie

import dev.odaridavid.smoovie.data.MoviesRepository
import dev.odaridavid.smoovie.data.model.Movie
import dev.odaridavid.smoovie.data.model.MoviesResponse

class FakeMoviesRepository(
    var movies: List<Movie> = emptyList(),
    var error: Exception? = null,
) : MoviesRepository {
    override suspend fun getPopularMovies(page: Int): MoviesResponse {
        error?.let { throw it }
        return MoviesResponse(
            page = page,
            results = movies,
            totalPages = 1,
            totalResults = movies.size,
        )
    }

    override suspend fun searchMovies(
        query: String,
        page: Int,
    ): MoviesResponse {
        error?.let { throw it }
        return MoviesResponse(
            page = page,
            results = movies,
            totalPages = 1,
            totalResults = movies.size,
        )
    }
}
