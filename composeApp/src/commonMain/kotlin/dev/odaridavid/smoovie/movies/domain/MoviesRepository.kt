package dev.odaridavid.smoovie.movies.domain

import dev.odaridavid.smoovie.movies.data.Genre
import dev.odaridavid.smoovie.movies.data.MovieDetail
import dev.odaridavid.smoovie.movies.data.MoviesResponse

interface MoviesRepository {
    suspend fun getPopularMovies(page: Int = 1): MoviesResponse

    suspend fun searchMovies(
        query: String,
        page: Int = 1,
    ): MoviesResponse

    suspend fun discoverMoviesByGenre(
        genreId: Int,
        page: Int = 1,
    ): MoviesResponse

    suspend fun getGenres(): List<Genre>

    suspend fun getMovieDetail(movieId: Int): MovieDetail
}
