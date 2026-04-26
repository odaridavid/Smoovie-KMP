package dev.odaridavid.smoovie.shows

import dev.odaridavid.smoovie.utils.toDisplayRating
import dev.odaridavid.smoovie.utils.toReadableDate
import dev.odaridavid.smoovie.shows.data.TvShow

data class TvGenreUiModel(
    val id: Int,
    val name: String,
)

data class TvShowUiModel(
    val id: Int,
    val name: String,
    val overview: String,
    val firstAirDate: String,
    val voteAverage: String,
    val backdropUrl: String?,
    val posterUrl: String? = null,
)

internal fun TvShow.toUiModel(
    backdropUrl: String?,
    posterUrl: String? = null,
) = TvShowUiModel(
    id = id,
    name = name,
    overview = overview,
    firstAirDate = firstAirDate.toReadableDate(),
    voteAverage = voteAverage.toDisplayRating(),
    backdropUrl = backdropUrl,
    posterUrl = posterUrl,
)
