package dev.odaridavid.smoovie.movies

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

private fun Double.toDisplayRating(): String {
    if (this == 0.0) return ""
    val rounded = kotlin.math.round(this * 10).toLong()
    return "${rounded / 10}.${rounded % 10}"
}

private fun String.toReadableDate(): String {
    val parts = split("-")
    if (parts.size != 3) return this
    val year = parts[0]
    val monthName =
        when (parts[1].toIntOrNull()) {
            1 -> "Jan"
            2 -> "Feb"
            3 -> "Mar"
            4 -> "Apr"
            5 -> "May"
            6 -> "Jun"
            7 -> "Jul"
            8 -> "Aug"
            9 -> "Sep"
            10 -> "Oct"
            11 -> "Nov"
            12 -> "Dec"
            else -> return this
        }
    val day = parts[2].trimStart('0').ifEmpty { "0" }
    return "$day $monthName $year"
}
