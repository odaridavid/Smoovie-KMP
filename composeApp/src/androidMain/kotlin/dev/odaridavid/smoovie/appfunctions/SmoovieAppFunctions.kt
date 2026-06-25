package dev.odaridavid.smoovie.appfunctions

import androidx.appfunctions.AppFunctionContext
import androidx.appfunctions.AppFunctionSerializable
import androidx.appfunctions.service.AppFunction
import dev.odaridavid.smoovie.movies.MovieUiModel
import dev.odaridavid.smoovie.movies.domain.SearchMoviesUseCase
import dev.odaridavid.smoovie.shows.TvShowUiModel
import dev.odaridavid.smoovie.shows.domain.SearchTvShowsUseCase
import dev.odaridavid.smoovie.watchlist.domain.MediaType
import dev.odaridavid.smoovie.watchlist.domain.ObserveWatchlistUseCase
import dev.odaridavid.smoovie.watchlist.domain.RemoveFromWatchlistUseCase
import dev.odaridavid.smoovie.watchlist.domain.ToggleWatchlistUseCase
import dev.odaridavid.smoovie.watchlist.domain.WatchlistEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

/**
 * A single result row from a TMDB title search.
 */
@AppFunctionSerializable
data class SmoovieTitle(
    /** The TMDB numeric identifier; required to add or remove this title from the watchlist. */
    val tmdbId: Int,
    /** The display title of the movie or show. */
    val title: String,
    /** Free-form summary of the title, as provided by TMDB; may be empty when TMDB has no overview. */
    val overview: String,
    /** Release date (movies) or first-air date (shows) in the user's locale; empty if TMDB has no date. */
    val releaseDate: String,
    /** Average TMDB rating formatted for display, e.g. "7.4"; empty if TMDB has no rating yet. */
    val voteAverage: String,
    /** Media kind: "movie" for films, "tv" for shows. Pass this to watchlist mutations. */
    val mediaType: String,
)

/**
 * A page of search results.
 */
@AppFunctionSerializable
data class SmoovieSearchPage(
    /** Results on this page, in TMDB relevance order; empty when no titles match. */
    val results: List<SmoovieTitle>,
    /** The 1-indexed page number returned by TMDB. */
    val page: Int,
    /** Total pages available for this query; pass `page + 1` to paginate while `page < totalPages`. */
    val totalPages: Int,
)

/**
 * Smoovie's AppFunctions surface — TMDB search and on-device watchlist management.
 */
class SmoovieAppFunctions(
    private val searchMovies: SearchMoviesUseCase,
    private val searchTvShows: SearchTvShowsUseCase,
    private val toggleWatchlist: ToggleWatchlistUseCase,
    private val removeFromWatchlist: RemoveFromWatchlistUseCase,
    private val observeWatchlist: ObserveWatchlistUseCase,
) {
    /**
     * Search TMDB for movies matching a free-form title query.
     * Required workflow: Call before "addToWatchlist" or "removeFromWatchlist" to resolve a title into a numeric tmdbId.
     *
     * @param appFunctionContext The execution context.
     * @param query Title to search for. Must be non-blank.
     * @param page 1-indexed page of results. Defaults to 1.
     * @return A page of movie results. `results` is empty when the query has no matches.
     * @throws IllegalArgumentException If the query is blank; ask the user to provide a non-empty title.
     */
    @AppFunction
    suspend fun searchMovies(
        appFunctionContext: AppFunctionContext,
        query: String,
        page: Int = 1,
    ): SmoovieSearchPage =
        withContext(Dispatchers.IO) {
            require(query.isNotBlank()) { "query must not be blank" }
            val moviesPage = searchMovies.invoke(query, page)
            SmoovieSearchPage(
                results = moviesPage.movies.map { it.toSmoovieTitle() },
                page = moviesPage.page,
                totalPages = moviesPage.totalPages,
            )
        }

    /**
     * Search TMDB for TV shows matching a free-form title query.
     * Required workflow: Call before "addToWatchlist" or "removeFromWatchlist" to resolve a title into a numeric tmdbId.
     *
     * @param appFunctionContext The execution context.
     * @param query Title to search for. Must be non-blank.
     * @param page 1-indexed page of results. Defaults to 1.
     * @return A page of TV show results. `results` is empty when the query has no matches.
     * @throws IllegalArgumentException If the query is blank; ask the user to provide a non-empty title.
     */
    @AppFunction
    suspend fun searchTvShows(
        appFunctionContext: AppFunctionContext,
        query: String,
        page: Int = 1,
    ): SmoovieSearchPage =
        withContext(Dispatchers.IO) {
            require(query.isNotBlank()) { "query must not be blank" }
            val showsPage = searchTvShows.invoke(query, page)
            SmoovieSearchPage(
                results = showsPage.tvShows.map { it.toSmoovieTitle() },
                page = showsPage.page,
                totalPages = showsPage.totalPages,
            )
        }

    /**
     * List the user's saved watchlist entries.
     *
     * @param appFunctionContext The execution context.
     * @param mediaType Optional filter: "movie" or "tv". When null, returns both kinds.
     * @return The current snapshot of watchlist entries. Empty when the watchlist has no matching items.
     */
    @AppFunction
    suspend fun listWatchlist(
        appFunctionContext: AppFunctionContext,
        mediaType: String? = null,
    ): List<SmoovieTitle> =
        withContext(Dispatchers.IO) {
            val typeFilter = mediaType?.parseMediaType()
            observeWatchlist
                .invoke()
                .first()
                .filter { typeFilter == null || it.mediaType == typeFilter }
                .map { it.toSmoovieTitle() }
        }

    /**
     * Add a TMDB title to the user's watchlist. Idempotent: adding an entry that is already present is a no-op.
     * Required workflow: Call "searchMovies" or "searchTvShows" first to obtain a valid tmdbId.
     *
     * @param appFunctionContext The execution context.
     * @param tmdbId The TMDB numeric identifier returned by a prior search.
     * @param mediaType "movie" or "tv"; must match the kind from the search result.
     * @throws IllegalArgumentException If the title cannot be resolved on TMDB; ask the user to confirm the title via a search call.
     */
    @AppFunction
    suspend fun addToWatchlist(
        appFunctionContext: AppFunctionContext,
        tmdbId: Int,
        mediaType: String,
    ) {
        withContext(Dispatchers.IO) {
            val type = mediaType.parseMediaType()
            val entry = fetchTitleAsEntry(tmdbId, type)
            toggleWatchlist.invokeIfAbsent(entry)
        }
    }

    /**
     * Remove a TMDB title from the user's watchlist. No-op if the entry is not present.
     * Required workflow: Call "listWatchlist" first if the agent does not already have the tmdbId.
     *
     * @param appFunctionContext The execution context.
     * @param tmdbId The TMDB numeric identifier of the entry to remove.
     * @param mediaType "movie" or "tv"; must match the entry's kind.
     */
    @AppFunction
    suspend fun removeFromWatchlist(
        appFunctionContext: AppFunctionContext,
        tmdbId: Int,
        mediaType: String,
    ) {
        withContext(Dispatchers.IO) {
            removeFromWatchlist.invoke(tmdbId, mediaType.parseMediaType())
        }
    }

    private suspend fun fetchTitleAsEntry(
        tmdbId: Int,
        type: MediaType,
    ): WatchlistEntry {
        val match =
            when (type) {
                MediaType.MOVIE -> searchMovies.lookupById(tmdbId)?.toWatchlistEntry()
                MediaType.TV -> searchTvShows.lookupById(tmdbId)?.toWatchlistEntry()
            }
        return match ?: throw IllegalArgumentException(
            "No ${type.storageKey} found on TMDB with id $tmdbId; have the user search again to pick a valid title.",
        )
    }

    private suspend fun ToggleWatchlistUseCase.invokeIfAbsent(entry: WatchlistEntry) {
        val current = observeWatchlist.invoke().first()
        val alreadyPresent = current.any { it.id == entry.id && it.mediaType == entry.mediaType }
        if (alreadyPresent) return
        when (entry.mediaType) {
            MediaType.MOVIE -> invoke(entry.toMovieUiModel())
            MediaType.TV -> invoke(entry.toTvShowUiModel())
        }
    }
}

private fun String.parseMediaType(): MediaType =
    when (this.lowercase()) {
        "movie" -> MediaType.MOVIE
        "tv" -> MediaType.TV
        else -> throw IllegalArgumentException("mediaType must be 'movie' or 'tv', got '$this'")
    }

private suspend fun SearchMoviesUseCase.lookupById(tmdbId: Int): MovieUiModel? {
    var page = 1
    repeat(MAX_LOOKUP_PAGES) {
        val result = invoke(tmdbId.toString(), page)
        result.movies.firstOrNull { it.id == tmdbId }?.let { return it }
        if (page >= result.totalPages) return null
        page += 1
    }
    return null
}

private suspend fun SearchTvShowsUseCase.lookupById(tmdbId: Int): TvShowUiModel? {
    var page = 1
    repeat(MAX_LOOKUP_PAGES) {
        val result = invoke(tmdbId.toString(), page)
        result.tvShows.firstOrNull { it.id == tmdbId }?.let { return it }
        if (page >= result.totalPages) return null
        page += 1
    }
    return null
}

private fun MovieUiModel.toSmoovieTitle() =
    SmoovieTitle(
        tmdbId = id,
        title = title,
        overview = overview,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        mediaType = MediaType.MOVIE.storageKey,
    )

private fun TvShowUiModel.toSmoovieTitle() =
    SmoovieTitle(
        tmdbId = id,
        title = name,
        overview = overview,
        releaseDate = firstAirDate,
        voteAverage = voteAverage,
        mediaType = MediaType.TV.storageKey,
    )

private fun WatchlistEntry.toSmoovieTitle() =
    SmoovieTitle(
        tmdbId = id,
        title = title,
        overview = overview,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        mediaType = mediaType.storageKey,
    )

private fun MovieUiModel.toWatchlistEntry() =
    WatchlistEntry(
        id = id,
        title = title,
        overview = overview,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        backdropUrl = backdropUrl,
        posterUrl = posterUrl,
        mediaType = MediaType.MOVIE,
    )

private fun TvShowUiModel.toWatchlistEntry() =
    WatchlistEntry(
        id = id,
        title = name,
        overview = overview,
        releaseDate = firstAirDate,
        voteAverage = voteAverage,
        backdropUrl = backdropUrl,
        posterUrl = posterUrl,
        mediaType = MediaType.TV,
    )

private fun WatchlistEntry.toMovieUiModel() =
    MovieUiModel(
        id = id,
        title = title,
        overview = overview,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        backdropUrl = backdropUrl,
        posterUrl = posterUrl,
    )

private fun WatchlistEntry.toTvShowUiModel() =
    TvShowUiModel(
        id = id,
        name = title,
        overview = overview,
        firstAirDate = releaseDate,
        voteAverage = voteAverage,
        backdropUrl = backdropUrl,
        posterUrl = posterUrl,
    )

private const val MAX_LOOKUP_PAGES = 3
