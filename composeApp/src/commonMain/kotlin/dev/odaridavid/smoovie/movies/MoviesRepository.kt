package dev.odaridavid.smoovie.movies

interface MoviesRepository {
    suspend fun getPopularMovies(page: Int = 1): MoviesResponse

    suspend fun searchMovies(
        query: String,
        page: Int = 1,
    ): MoviesResponse
}
