package dev.odaridavid.smoovie.movies.data

import dev.odaridavid.smoovie.TMDB_BASE_URL
import dev.odaridavid.smoovie.movies.domain.MoviesRepository
import dev.odaridavid.smoovie.utils.TtlCache
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class MoviesRepositoryImpl(
    private val client: HttpClient,
) : MoviesRepository {
    private val popularCache = TtlCache<Int, MoviesResponse>(CACHE_TTL_MS)
    private val searchCache = TtlCache<SearchKey, MoviesResponse>(CACHE_TTL_MS)
    private val genreDiscoverCache = TtlCache<GenreKey, MoviesResponse>(CACHE_TTL_MS)
    private val genresCache = TtlCache<Unit, List<Genre>>(CACHE_TTL_MS)
    private val detailCache = TtlCache<Int, MovieDetail>(CACHE_TTL_MS)
    private val watchProvidersCache = TtlCache<Int, WatchProvidersResponse>(CACHE_TTL_MS)

    override suspend fun getPopularMovies(page: Int): MoviesResponse =
        popularCache.getOrFetch(page) {
            client
                .get(Path.POPULAR_MOVIES) {
                    parameter(Parameter.PAGE, page)
                }.body()
        }

    override suspend fun searchMovies(
        query: String,
        page: Int,
    ): MoviesResponse =
        searchCache.getOrFetch(SearchKey(query, page)) {
            client
                .get(Path.SEARCH_MOVIES) {
                    parameter(Parameter.QUERY, query)
                    parameter(Parameter.PAGE, page)
                }.body()
        }

    override suspend fun discoverMoviesByGenre(
        genreId: Int,
        page: Int,
    ): MoviesResponse =
        genreDiscoverCache.getOrFetch(GenreKey(genreId, page)) {
            client
                .get(Path.DISCOVER_MOVIES) {
                    parameter(Parameter.WITH_GENRES, genreId)
                    parameter(Parameter.SORT_BY, "popularity.desc")
                    parameter(Parameter.PAGE, page)
                }.body()
        }

    override suspend fun getGenres(): List<Genre> =
        genresCache.getOrFetch(Unit) {
            client.get(Path.MOVIE_GENRES).body<GenresResponse>().genres
        }

    override suspend fun getMovieDetail(movieId: Int): MovieDetail =
        detailCache.getOrFetch(movieId) {
            client
                .get("${Path.MOVIE_DETAIL}/$movieId") {
                    parameter(Parameter.APPEND_TO_RESPONSE, "credits,reviews,videos,recommendations,similar,release_dates")
                }.body()
        }

    override suspend fun getWatchProviders(movieId: Int): WatchProvidersResponse =
        watchProvidersCache.getOrFetch(movieId) {
            client.get("${Path.MOVIE_DETAIL}/$movieId/watch/providers").body()
        }

    private data class SearchKey(
        val query: String,
        val page: Int,
    )

    private data class GenreKey(
        val genreId: Int,
        val page: Int,
    )

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

    private companion object {
        const val CACHE_TTL_MS = 60 * 60 * 1_000L
    }
}
