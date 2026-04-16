package dev.odaridavid.smoovie.movies

import dev.odaridavid.smoovie.movies.data.MovieDetail
import dev.odaridavid.smoovie.movies.data.Video

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
    val reviews: List<ReviewUiModel> = emptyList(),
    val trailers: List<TrailerUiModel> = emptyList(),
)

data class CastMemberUiModel(
    val id: Int,
    val name: String,
    val character: String,
    val profileUrl: String?,
)

data class ReviewUiModel(
    val id: String,
    val author: String,
    val date: String,
    val rating: String,
    val content: String,
)

data class TrailerUiModel(
    val id: String,
    val name: String,
    val thumbnailUrl: String,
    val watchUrl: String,
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
    reviews = reviews?.results
        ?.take(MAX_REVIEWS_DISPLAY)
        ?.map { review ->
            ReviewUiModel(
                id = review.id,
                author = review.author.ifBlank { review.authorDetails?.username.orEmpty() },
                date = review.createdAt.take(10).toReadableDate(),
                rating = review.authorDetails?.rating?.toDisplayRating().orEmpty(),
                content = review.content.trim(),
            )
        }
        ?.filter { it.content.isNotBlank() }
        ?: emptyList(),
    trailers = videos?.results
        ?.filter { it.site.equals(YOUTUBE_SITE, ignoreCase = true) && it.key.isNotBlank() }
        ?.sortedWith(
            compareByDescending<Video> { it.type.equals(TRAILER_TYPE, ignoreCase = true) }
                .thenByDescending { it.official },
        )
        ?.take(MAX_TRAILERS_DISPLAY)
        ?.map { video ->
            TrailerUiModel(
                id = video.id,
                name = video.name,
                thumbnailUrl = "https://img.youtube.com/vi/${video.key}/mqdefault.jpg",
                watchUrl = "https://www.youtube.com/watch?v=${video.key}",
            )
        } ?: emptyList(),
)

private fun Int.toFormattedCount(): String =
    toString().reversed().chunked(3).joinToString(",").reversed()

private const val MAX_CAST_DISPLAY = 20
private const val MAX_REVIEWS_DISPLAY = 3
private const val MAX_TRAILERS_DISPLAY = 5
private const val DIRECTOR_JOB = "Director"
private const val YOUTUBE_SITE = "YouTube"
private const val TRAILER_TYPE = "Trailer"
