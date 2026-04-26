package dev.odaridavid.smoovie.movies

import dev.odaridavid.smoovie.movies.data.Movie
import dev.odaridavid.smoovie.utils.toDisplayRating
import dev.odaridavid.smoovie.utils.toReadableDate

data class GenreUiModel(
    val id: Int,
    val name: String,
)

data class MovieUiModel(
    val id: Int,
    val title: String,
    val overview: String,
    val releaseDate: String,
    val voteAverage: String,
    val backdropUrl: String?,
    val posterUrl: String? = null,
)

internal fun Movie.toUiModel(
    backdropUrl: String?,
    posterUrl: String? = null,
) = MovieUiModel(
    id = id,
    title = title,
    overview = overview,
    releaseDate = releaseDate.toReadableDate(),
    voteAverage = voteAverage.toDisplayRating(),
    backdropUrl = backdropUrl,
    posterUrl = posterUrl,
)

