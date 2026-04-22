package dev.odaridavid.smoovie.watchlist.domain

import dev.odaridavid.smoovie.movies.MovieUiModel
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
