package dev.odaridavid.smoovie.shows

import dev.odaridavid.smoovie.movies.CastMemberUiModel
import dev.odaridavid.smoovie.movies.ReviewUiModel
import dev.odaridavid.smoovie.movies.TrailerUiModel
import dev.odaridavid.smoovie.movies.WatchProviderUiModel
import dev.odaridavid.smoovie.movies.data.Video
import dev.odaridavid.smoovie.movies.toDisplayRating
import dev.odaridavid.smoovie.movies.toReadableDate
import dev.odaridavid.smoovie.shows.data.TvShow
import dev.odaridavid.smoovie.shows.data.TvShowDetail

data class TvShowDetailUiModel(
    val id: Int,
    val name: String,
    val overview: String,
    val tagline: String,
    val firstAirDate: String,
    val lastAirDate: String,
    val yearsRange: String,
    val voteAverage: String,
    val voteCount: String,
    val backdropUrl: String?,
    val posterUrl: String?,
    val seasonsLabel: String,
    val genres: String,
    val networks: String,
    val ageRating: String = "",
    val seasons: List<SeasonUiModel> = emptyList(),
    val cast: List<CastMemberUiModel> = emptyList(),
    val reviews: List<ReviewUiModel> = emptyList(),
    val trailers: List<TrailerUiModel> = emptyList(),
    val similar: List<TvShowUiModel> = emptyList(),
    val streamingProviders: List<WatchProviderUiModel> = emptyList(),
    val rentBuyProviders: List<WatchProviderUiModel> = emptyList(),
    val watchProvidersLink: String? = null,
    val keywords: List<String> = emptyList(),
)

data class SeasonUiModel(
    val id: Int,
    val seasonNumber: Int,
    val name: String,
    val year: String,
    val episodeCountLabel: String,
    val posterUrl: String?,
)

internal fun TvShowDetail.toDetailUiModel(
    backdropUrl: String?,
    posterUrl: String?,
    profileUrlResolver: (String?) -> String? = { null },
    seasonPosterResolver: (String?) -> String? = { null },
    similarBackdropResolver: (String?) -> String? = { null },
    similarPosterResolver: (String?) -> String? = { null },
    presentLabel: String,
    streamingProviders: List<WatchProviderUiModel> = emptyList(),
    rentBuyProviders: List<WatchProviderUiModel> = emptyList(),
    watchProvidersLink: String? = null,
    keywords: List<String> = emptyList(),
) = TvShowDetailUiModel(
    id = id,
    name = name,
    overview = overview,
    tagline = tagline,
    firstAirDate = firstAirDate.toReadableDate(),
    lastAirDate = lastAirDate.toReadableDate(),
    yearsRange = formatYearsRange(firstAirDate, lastAirDate, inProduction, presentLabel),
    voteAverage = voteAverage.toDisplayRating(),
    voteCount = voteCount.toFormattedCount(),
    backdropUrl = backdropUrl,
    posterUrl = posterUrl,
    seasonsLabel = formatSeasonsLabel(numberOfSeasons, numberOfEpisodes),
    genres = genres.joinToString { it.name },
    networks = networks.joinToString { it.name },
    ageRating =
        contentRatings?.results
            ?.firstOrNull { it.countryCode == "DE" }
            ?.rating
            ?.takeIf { it.isNotBlank() }
            ?.let { if (it.startsWith("FSK")) it else "FSK $it" }
            ?: "",
    seasons =
        seasons
            .filter { it.seasonNumber > 0 }
            .map { season ->
                SeasonUiModel(
                    id = season.id,
                    seasonNumber = season.seasonNumber,
                    name = season.name,
                    year = season.airDate.orEmpty().take(4),
                    episodeCountLabel = "${season.episodeCount} episodes",
                    posterUrl = seasonPosterResolver(season.posterPath),
                )
            },
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
            backdropResolver = similarBackdropResolver,
            posterResolver = similarPosterResolver,
        ),
    streamingProviders = streamingProviders,
    rentBuyProviders = rentBuyProviders,
    watchProvidersLink = watchProvidersLink,
    keywords = keywords,
)

private fun buildSimilarRail(
    recommendations: List<TvShow>,
    similar: List<TvShow>,
    excludeId: Int,
    backdropResolver: (String?) -> String?,
    posterResolver: (String?) -> String?,
): List<TvShowUiModel> {
    val seen = mutableSetOf(excludeId)
    val merged = mutableListOf<TvShow>()
    for (show in recommendations + similar) {
        if (merged.size == MAX_SIMILAR_DISPLAY) break
        if (seen.add(show.id)) merged += show
    }
    return merged.map { show ->
        show.toUiModel(
            backdropUrl = backdropResolver(show.backdropPath),
            posterUrl = posterResolver(show.posterPath),
        )
    }
}

private fun formatYearsRange(
    firstAirDate: String,
    lastAirDate: String,
    inProduction: Boolean,
    presentLabel: String,
): String {
    val firstYear = firstAirDate.take(4).takeIf { it.length == 4 } ?: return ""
    val lastYear = lastAirDate.take(4).takeIf { it.length == 4 }
    return when {
        inProduction -> "$firstYear – $presentLabel"
        lastYear == null || lastYear == firstYear -> firstYear
        else -> "$firstYear – $lastYear"
    }
}

private fun formatSeasonsLabel(
    numberOfSeasons: Int,
    numberOfEpisodes: Int,
): String {
    if (numberOfSeasons <= 0) return ""
    val seasonsPart = if (numberOfSeasons == 1) "1 season" else "$numberOfSeasons seasons"
    val episodesPart = if (numberOfEpisodes == 1) "1 episode" else "$numberOfEpisodes episodes"
    return "$seasonsPart · $episodesPart"
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
private const val YOUTUBE_SITE = "YouTube"
private const val TRAILER_TYPE = "Trailer"
