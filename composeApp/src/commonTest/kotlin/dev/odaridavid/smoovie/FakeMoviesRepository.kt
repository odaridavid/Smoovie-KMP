package dev.odaridavid.smoovie

import dev.odaridavid.smoovie.movies.data.Movie
import dev.odaridavid.smoovie.movies.data.MovieDetail
import dev.odaridavid.smoovie.movies.domain.MoviesRepository
import dev.odaridavid.smoovie.movies.data.MoviesResponse

class FakeMoviesRepository(
    var movies: List<Movie> = emptyList(),
    var movieDetail: MovieDetail? = null,
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

    override suspend fun getMovieDetail(movieId: Int): MovieDetail {
        error?.let { throw it }
        return movieDetail ?: error("No movie detail configured")
    }
}
