package dev.odaridavid.smoovie.data

import dev.odaridavid.smoovie.data.model.MoviesResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

private const val TMDB_BASE_URL = "https://api.themoviedb.org/3"

class MoviesRepositoryImpl(
    private val client: HttpClient,
) : MoviesRepository {
    override suspend fun getPopularMovies(page: Int): MoviesResponse =
        client
            .get("$TMDB_BASE_URL/movie/popular") {
                parameter("page", page)
            }.body()

    override suspend fun searchMovies(
        query: String,
        page: Int,
    ): MoviesResponse =
        client
            .get("$TMDB_BASE_URL/search/movie") {
                parameter("query", query)
                parameter("page", page)
            }.body()
}
