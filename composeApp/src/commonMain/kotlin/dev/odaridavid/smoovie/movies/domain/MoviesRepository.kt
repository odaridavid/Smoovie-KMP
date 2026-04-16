package dev.odaridavid.smoovie.movies.domain

import dev.odaridavid.smoovie.movies.data.MovieDetail
import dev.odaridavid.smoovie.movies.data.MoviesResponse

interface MoviesRepository {
    suspend fun getPopularMovies(page: Int = 1): MoviesResponse

    suspend fun searchMovies(
        query: String,
        page: Int = 1,
    ): MoviesResponse

    suspend fun getMovieDetail(movieId: Int): MovieDetail
}
