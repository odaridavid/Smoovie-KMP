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
    val director: String = "",
    val cast: List<CastMemberUiModel> = emptyList(),
)

data class CastMemberUiModel(
    val id: Int,
    val name: String,
    val character: String,
    val profileUrl: String?,
)

internal fun MovieDetail.toDetailUiModel(
    backdropUrl: String?,
    posterUrl: String?,
    profileUrlResolver: (String?) -> String? = { null },
) = MovieDetailUiModel(
    id = id,
    title = title,
    overview = overview,
    releaseDate = releaseDate.toReadableDate(),
    voteAverage = voteAverage.toDisplayRating(),
    voteCount = voteCount.toFormattedCount(),
    backdropUrl = backdropUrl,
    posterUrl = posterUrl,
    runtime = runtime?.let { "${it / 60}h ${it % 60}m" } ?: "",
    tagline = tagline,
    genres = genres.joinToString { it.name },
    director = credits?.crew
        ?.firstOrNull { it.job == DIRECTOR_JOB }
        ?.name ?: "",
    cast = credits?.cast
        ?.sortedBy { it.order }
        ?.take(MAX_CAST_DISPLAY)
        ?.map { member ->
            CastMemberUiModel(
                id = member.id,
                name = member.name,
                character = member.character,
                profileUrl = profileUrlResolver(member.profilePath),
            )
        } ?: emptyList(),
)

private fun Int.toFormattedCount(): String =
    toString().reversed().chunked(3).joinToString(",").reversed()

private const val MAX_CAST_DISPLAY = 20
private const val DIRECTOR_JOB = "Director"
