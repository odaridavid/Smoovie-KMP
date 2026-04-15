package dev.odaridavid.smoovie.movies

data class MovieDetailUiModel(
    val id: Int,
    val title: String,
    val overview: String,
    val releaseDate: String,
    val voteAverage: String,
    val voteCount: String,
    val backdropUrl: String?,
    val posterUrl: String?,
    val runtime: String,
    val tagline: String,
    val genres: String,
)

internal fun MovieDetail.toDetailUiModel(
    backdropUrl: String?,
    posterUrl: String?,
) = MovieDetailUiModel(
    id = id,
    title = title,
    overview = overview,
    releaseDate = releaseDate.toReadableDate(),
    voteAverage = voteAverage.toDisplayRating(),
    voteCount = voteCount.toString(),
    backdropUrl = backdropUrl,
    posterUrl = posterUrl,
    runtime = runtime?.let { "${it / 60}h ${it % 60}m" } ?: "",
    tagline = tagline,
    genres = genres.joinToString { it.name },
)
