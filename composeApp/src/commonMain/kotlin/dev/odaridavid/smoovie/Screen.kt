package dev.odaridavid.smoovie

import dev.odaridavid.smoovie.movies.MovieUiModel
import dev.odaridavid.smoovie.person.PersonSummaryUiModel
import kotlinx.serialization.Serializable

@Serializable
internal data object MoviesRoute

@Serializable
internal data class MovieDetailRoute(
    val id: Int,
    val title: String,
    val overview: String,
    val releaseDate: String,
    val voteAverage: String,
    val backdropUrl: String?,
    val posterUrl: String?,
)

@Serializable
internal data class PersonDetailRoute(
    val id: Int,
    val name: String,
    val profileUrl: String?,
)

internal fun MovieUiModel.toRoute() =
    MovieDetailRoute(
        id = id,
        title = title,
        overview = overview,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        backdropUrl = backdropUrl,
        posterUrl = posterUrl,
    )

internal fun MovieDetailRoute.toUiModel() =
    MovieUiModel(
        id = id,
        title = title,
        overview = overview,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        backdropUrl = backdropUrl,
        posterUrl = posterUrl,
    )

internal fun PersonSummaryUiModel.toRoute() =
    PersonDetailRoute(
        id = id,
        name = name,
        profileUrl = profileUrl,
    )

internal fun PersonDetailRoute.toUiModel() =
    PersonSummaryUiModel(
        id = id,
        name = name,
        profileUrl = profileUrl,
    )
