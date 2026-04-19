package dev.odaridavid.smoovie.movies.data

import dev.odaridavid.smoovie.TMDB_BASE_URL
import dev.odaridavid.smoovie.movies.domain.MoviesRepository
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

    override suspend fun discoverMoviesByGenre(
        genreId: Int,
        page: Int,
    ): MoviesResponse =
        client
            .get(Path.DISCOVER_MOVIES) {
                parameter(Parameter.WITH_GENRES, genreId)
                parameter(Parameter.SORT_BY, "popularity.desc")
                parameter(Parameter.PAGE, page)
            }.body()

    override suspend fun getGenres(): List<Genre> = client.get(Path.MOVIE_GENRES).body<GenresResponse>().genres

    override suspend fun getMovieDetail(movieId: Int): MovieDetail =
        client
            .get("${Path.MOVIE_DETAIL}/$movieId") {
                parameter(Parameter.APPEND_TO_RESPONSE, "credits,reviews,videos")
            }.body()

    private object Path {
        const val POPULAR_MOVIES = "${TMDB_BASE_URL}/movie/popular"
        const val SEARCH_MOVIES = "${TMDB_BASE_URL}/search/movie"
        const val DISCOVER_MOVIES = "${TMDB_BASE_URL}/discover/movie"
        const val MOVIE_GENRES = "${TMDB_BASE_URL}/genre/movie/list"
        const val MOVIE_DETAIL = "${TMDB_BASE_URL}/movie"
    }

    private object Parameter {
        const val PAGE = "page"
        const val QUERY = "query"
        const val WITH_GENRES = "with_genres"
        const val SORT_BY = "sort_by"
        const val APPEND_TO_RESPONSE = "append_to_response"
    }
}
