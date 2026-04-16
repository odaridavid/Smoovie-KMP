package dev.odaridavid.smoovie.movies

import dev.odaridavid.smoovie.TMDB_BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class MoviesRepositoryImpl(
    private val client: HttpClient,
) : MoviesRepository {
    override suspend fun getPopularMovies(page: Int): MoviesResponse =
        client
            .get(Path.POPULAR_MOVIES) {
                parameter(Parameter.PAGE, page)
            }.body()

    override suspend fun searchMovies(
        query: String,
        page: Int,
    ): MoviesResponse =
        client
            .get(Path.SEARCH_MOVIES) {
                parameter(Parameter.QUERY, query)
                parameter(Parameter.PAGE, page)
            }.body()

    override suspend fun getMovieDetail(movieId: Int): MovieDetail =
        client
            .get("${Path.MOVIE_DETAIL}/$movieId") {
                parameter(Parameter.APPEND_TO_RESPONSE, "credits")
            }.body()

    private object Path {
        const val POPULAR_MOVIES = "$TMDB_BASE_URL/movie/popular"
        const val SEARCH_MOVIES = "$TMDB_BASE_URL/search/movie"
        const val MOVIE_DETAIL = "$TMDB_BASE_URL/movie"
    }

    private object Parameter {
        const val PAGE = "page"
        const val QUERY = "query"
        const val APPEND_TO_RESPONSE = "append_to_response"
    }
}
