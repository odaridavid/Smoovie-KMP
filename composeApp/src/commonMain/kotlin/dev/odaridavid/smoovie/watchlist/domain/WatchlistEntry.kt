package dev.odaridavid.smoovie.watchlist.domain

import dev.odaridavid.smoovie.movies.MovieUiModel
import dev.odaridavid.smoovie.shows.TvShowUiModel
import kotlinx.serialization.Serializable

@Serializable
data class WatchlistEntry(
    val id: Int,
    val title: String,
    val overview: String,
    val releaseDate: String,
    val voteAverage: String,
    val backdropUrl: String?,
    val posterUrl: String?,
    val mediaType: MediaType,
)

internal fun MovieUiModel.toWatchlistEntry() =
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

internal fun WatchlistEntry.toMovieUiModel() =
    MovieUiModel(
        id = id,
        title = title,
        overview = overview,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        backdropUrl = backdropUrl,
        posterUrl = posterUrl,
    )

internal fun TvShowUiModel.toWatchlistEntry() =
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

internal fun WatchlistEntry.toTvShowUiModel() =
    TvShowUiModel(
        id = id,
        name = title,
        overview = overview,
        firstAirDate = releaseDate,
        voteAverage = voteAverage,
        backdropUrl = backdropUrl,
        posterUrl = posterUrl,
    )
