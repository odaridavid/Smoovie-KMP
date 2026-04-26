package dev.odaridavid.smoovie.movies

import dev.odaridavid.smoovie.movies.data.Movie
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
    val ageRating: String = "",
    val director: String = "",
    val cast: List<CastMemberUiModel> = emptyList(),
    val reviews: List<ReviewUiModel> = emptyList(),
    val trailers: List<TrailerUiModel> = emptyList(),
    val similar: List<MovieUiModel> = emptyList(),
    val streamingProviders: List<WatchProviderUiModel> = emptyList(),
    val rentBuyProviders: List<WatchProviderUiModel> = emptyList(),
    val watchProvidersLink: String? = null,
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
    val videoKey: String,
    val thumbnailUrl: String,
)

data class WatchProviderUiModel(
    val name: String,
    val logoUrl: String?,
)

internal fun MovieDetail.toDetailUiModel(
    backdropUrl: String?,
    posterUrl: String?,
    profileUrlResolver: (String?) -> String? = { null },
    movieBackdropUrlResolver: (String?) -> String? = { null },
    moviePosterUrlResolver: (String?) -> String? = { null },
    streamingProviders: List<WatchProviderUiModel> = emptyList(),
    rentBuyProviders: List<WatchProviderUiModel> = emptyList(),
    watchProvidersLink: String? = null,
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
    ageRating =
        releaseDates
            ?.results
            ?.firstOrNull { it.countryCode == "DE" }
            ?.releaseDates
            ?.firstOrNull { it.type == THEATRICAL_RELEASE_TYPE && it.certification.isNotBlank() }
            ?.certification
            ?.let { "FSK $it" }
            ?: "",
    director =
        credits
            ?.crew
            ?.firstOrNull { it.job == DIRECTOR_JOB }
            ?.name ?: "",
    cast =
        credits
            ?.cast
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
    reviews =
        reviews
            ?.results
            ?.take(MAX_REVIEWS_DISPLAY)
            ?.map { review ->
                ReviewUiModel(
                    id = review.id,
                    author = review.author.ifBlank { review.authorDetails?.username.orEmpty() },
                    date = review.createdAt.take(10).toReadableDate(),
                    rating =
                        review.authorDetails
                            ?.rating
                            ?.toDisplayRating()
                            .orEmpty(),
                    content = review.content.trim(),
                )
            }?.filter { it.content.isNotBlank() }
            ?: emptyList(),
    trailers =
        videos
            ?.results
            ?.filter { it.site.equals(YOUTUBE_SITE, ignoreCase = true) && it.key.isNotBlank() }
            ?.sortedWith(
                compareByDescending<Video> { it.type.equals(TRAILER_TYPE, ignoreCase = true) }
                    .thenByDescending { it.official },
            )?.take(MAX_TRAILERS_DISPLAY)
            ?.map { video ->
                TrailerUiModel(
                    id = video.id,
                    name = video.name,
                    videoKey = video.key,
                    thumbnailUrl = "https://img.youtube.com/vi/${video.key}/mqdefault.jpg",
                )
            } ?: emptyList(),
    similar =
        buildSimilarRail(
            recommendations = recommendations?.results.orEmpty(),
            similar = similar?.results.orEmpty(),
            excludeId = id,
            backdropResolver = movieBackdropUrlResolver,
            posterResolver = moviePosterUrlResolver,
        ),
    streamingProviders = streamingProviders,
    rentBuyProviders = rentBuyProviders,
    watchProvidersLink = watchProvidersLink,
)

private fun buildSimilarRail(
    recommendations: List<Movie>,
    similar: List<Movie>,
    excludeId: Int,
    backdropResolver: (String?) -> String?,
    posterResolver: (String?) -> String?,
): List<MovieUiModel> {
    val seen = mutableSetOf(excludeId)
    val merged = mutableListOf<Movie>()
    for (movie in recommendations + similar) {
        if (merged.size == MAX_SIMILAR_DISPLAY) break
        if (seen.add(movie.id)) merged += movie
    }
    return merged.map { movie ->
        movie.toUiModel(
            backdropUrl = backdropResolver(movie.backdropPath),
            posterUrl = posterResolver(movie.posterPath),
        )
    }
}

private fun Int.toFormattedCount(): String =
    toString()
        .reversed()
        .chunked(3)
        .joinToString(",")
        .reversed()

private const val MAX_CAST_DISPLAY = 20
private const val MAX_REVIEWS_DISPLAY = 3
private const val MAX_TRAILERS_DISPLAY = 5
private const val MAX_SIMILAR_DISPLAY = 5
private const val DIRECTOR_JOB = "Director"
private const val YOUTUBE_SITE = "YouTube"
private const val TRAILER_TYPE = "Trailer"
private const val THEATRICAL_RELEASE_TYPE = 3
