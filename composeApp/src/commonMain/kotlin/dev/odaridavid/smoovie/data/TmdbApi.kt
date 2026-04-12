package dev.odaridavid.smoovie.data

import dev.odaridavid.smoovie.data.model.MoviesResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

// Use the API Read Access Token from https://www.themoviedb.org/settings/api
private const val TMDB_BASE_URL = "https://api.themoviedb.org/3"

class TmdbApi(
    private val apiKey: String,
) {
    private val client =
        HttpClient {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true // ignore fields TMDB sends that we don't model yet
                        coerceInputValues = true
                    },
                )
            }
            install(Logging) {
                level = LogLevel.HEADERS // switch to LogLevel.BODY to debug response payloads
            }
            install(DefaultRequest) {
                header(HttpHeaders.Authorization, "Bearer $apiKey")
            }
        }

    suspend fun getPopularMovies(page: Int = 1): MoviesResponse =
        client
            .get("$TMDB_BASE_URL/movie/popular") {
                parameter("page", page)
            }.body()

    suspend fun searchMovies(
        query: String,
        page: Int = 1,
    ): MoviesResponse =
        client
            .get("$TMDB_BASE_URL/search/movie") {
                parameter("query", query)
                parameter("page", page)
            }.body()
}
