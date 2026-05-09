package dev.odaridavid.smoovie

import dev.odaridavid.smoovie.movies.MovieUiModel
import dev.odaridavid.smoovie.person.PersonSummaryUiModel
import dev.odaridavid.smoovie.shows.TvShowUiModel
import kotlinx.serialization.Serializable

@Serializable
internal data object MoviesRoute

@Serializable
internal data object ShowsRoute

@Serializable
internal data object WatchlistRoute

@Serializable
internal data object SettingsRoute

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

@Serializable
internal data class PersonFilmographyRoute(
    val personId: Int,
    val personName: String,
    val mediaType: String,
)

@Serializable
internal data class TvShowDetailRoute(
    val id: Int,
    val name: String,
    val overview: String,
    val firstAirDate: String,
    val voteAverage: String,
    val backdropUrl: String?,
    val posterUrl: String?,
)

@Serializable
internal data class TvSeasonDetailRoute(
    val tvShowId: Int,
    val seasonNumber: Int,
    val seasonName: String,
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

internal fun TvShowUiModel.toRoute() =
    TvShowDetailRoute(
        id = id,
        name = name,
        overview = overview,
        firstAirDate = firstAirDate,
        voteAverage = voteAverage,
        backdropUrl = backdropUrl,
        posterUrl = posterUrl,
    )

internal fun TvShowDetailRoute.toUiModel() =
    TvShowUiModel(
        id = id,
        name = name,
        overview = overview,
        firstAirDate = firstAirDate,
        voteAverage = voteAverage,
        backdropUrl = backdropUrl,
        posterUrl = posterUrl,
    )
