package dev.odaridavid.smoovie.shows.data

import dev.odaridavid.smoovie.TMDB_BASE_URL
import dev.odaridavid.smoovie.movies.data.WatchProvidersResponse
import dev.odaridavid.smoovie.shows.domain.TvShowsRepository
import dev.odaridavid.smoovie.utils.TtlCache
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class TvShowsRepositoryImpl(
    private val client: HttpClient,
) : TvShowsRepository {
    private val popularCache = TtlCache<Int, TvShowsResponse>(CACHE_TTL_MS)
    private val searchCache = TtlCache<SearchKey, TvShowsResponse>(CACHE_TTL_MS)
    private val genreDiscoverCache = TtlCache<GenreKey, TvShowsResponse>(CACHE_TTL_MS)
    private val genresCache = TtlCache<Unit, List<TvGenre>>(CACHE_TTL_MS)
    private val detailCache = TtlCache<Int, TvShowDetail>(CACHE_TTL_MS)
    private val seasonDetailCache = TtlCache<SeasonKey, SeasonDetail>(CACHE_TTL_MS)
    private val watchProvidersCache = TtlCache<Int, WatchProvidersResponse>(CACHE_TTL_MS)

    override suspend fun getPopularTvShows(page: Int): TvShowsResponse =
        popularCache.getOrFetch(page) {
            client
                .get(Path.POPULAR_TV) {
                    parameter(Parameter.PAGE, page)
                }.body()
        }

    override suspend fun searchTvShows(
        query: String,
        page: Int,
    ): TvShowsResponse =
        searchCache.getOrFetch(SearchKey(query, page)) {
            client
                .get(Path.SEARCH_TV) {
                    parameter(Parameter.QUERY, query)
                    parameter(Parameter.PAGE, page)
                }.body()
        }

    override suspend fun discoverTvShowsByGenre(
        genreId: Int,
        page: Int,
    ): TvShowsResponse =
        genreDiscoverCache.getOrFetch(GenreKey(genreId, page)) {
            client
                .get(Path.DISCOVER_TV) {
                    parameter(Parameter.WITH_GENRES, genreId)
                    parameter(Parameter.SORT_BY, "popularity.desc")
                    parameter(Parameter.PAGE, page)
                }.body()
        }

    override suspend fun getGenres(): List<TvGenre> =
        genresCache.getOrFetch(Unit) {
            client.get(Path.TV_GENRES).body<TvGenresResponse>().genres
        }

    override suspend fun getTvShowDetail(tvShowId: Int): TvShowDetail =
        detailCache.getOrFetch(tvShowId) {
            client
                .get("${Path.TV_DETAIL}/$tvShowId") {
                    parameter(Parameter.APPEND_TO_RESPONSE, "credits,reviews,videos,recommendations,similar,content_ratings")
                }.body()
        }

    override suspend fun getSeasonDetail(
        tvShowId: Int,
        seasonNumber: Int,
    ): SeasonDetail =
        seasonDetailCache.getOrFetch(SeasonKey(tvShowId, seasonNumber)) {
            client.get("${Path.TV_DETAIL}/$tvShowId/season/$seasonNumber").body()
        }

    override suspend fun getWatchProviders(tvShowId: Int): WatchProvidersResponse =
        watchProvidersCache.getOrFetch(tvShowId) {
            client.get("${Path.TV_DETAIL}/$tvShowId/watch/providers").body()
        }

    private data class SearchKey(
        val query: String,
        val page: Int,
    )

    private data class GenreKey(
        val genreId: Int,
        val page: Int,
    )

    private data class SeasonKey(
        val tvShowId: Int,
        val seasonNumber: Int,
    )

    private object Path {
        const val POPULAR_TV = "${TMDB_BASE_URL}/tv/popular"
        const val SEARCH_TV = "${TMDB_BASE_URL}/search/tv"
        const val DISCOVER_TV = "${TMDB_BASE_URL}/discover/tv"
        const val TV_GENRES = "${TMDB_BASE_URL}/genre/tv/list"
        const val TV_DETAIL = "${TMDB_BASE_URL}/tv"
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
