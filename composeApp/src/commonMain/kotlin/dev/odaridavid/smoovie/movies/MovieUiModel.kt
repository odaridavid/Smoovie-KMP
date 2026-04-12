package dev.odaridavid.smoovie.movies

data class MovieUiModel(
    val id: Int,
    val title: String,
    val overview: String,
    val releaseDate: String,
    val voteAverage: Double,
    val backdropUrl: String?,
)

internal fun Movie.toUiModel(backdropUrl: String?) =
    MovieUiModel(
        id = id,
        title = title,
        overview = overview,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        backdropUrl = backdropUrl,
    )
