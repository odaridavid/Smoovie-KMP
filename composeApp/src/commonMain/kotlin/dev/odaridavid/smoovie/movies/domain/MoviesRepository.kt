package dev.odaridavid.smoovie.movies.domain

import dev.odaridavid.smoovie.movies.data.Genre
import dev.odaridavid.smoovie.movies.data.KeywordsResponse
import dev.odaridavid.smoovie.movies.data.MovieDetail
import dev.odaridavid.smoovie.movies.data.MoviesResponse
import dev.odaridavid.smoovie.shared.data.WatchProvidersResponse

interface MoviesRepository {
    suspend fun getPopularMovies(page: Int = 1): MoviesResponse

    suspend fun searchMovies(
        query: String,
        page: Int = 1,
    ): MoviesResponse

    suspend fun discoverMovies(
        genreId: Int?,
        sortBy: String,
        minRating: Float,
        page: Int = 1,
    ): MoviesResponse

    suspend fun getGenres(): List<Genre>

    suspend fun getMovieDetail(movieId: Int): MovieDetail

    suspend fun getWatchProviders(movieId: Int): WatchProvidersResponse

    suspend fun getMovieKeywords(movieId: Int): KeywordsResponse

    suspend fun getTrendingMovies(): MoviesResponse
}
