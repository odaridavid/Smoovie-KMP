package dev.odaridavid.smoovie.data

import dev.odaridavid.smoovie.data.model.MoviesResponse

interface MoviesRepository {
    suspend fun getPopularMovies(page: Int = 1): MoviesResponse

    suspend fun searchMovies(
        query: String,
        page: Int = 1,
    ): MoviesResponse
}
