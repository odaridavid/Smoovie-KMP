package dev.odaridavid.smoovie.movies.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieDetail(
    val id: Int,
    val title: String,
    val overview: String,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("release_date") val releaseDate: String = "",
    @SerialName("vote_average") val voteAverage: Double = 0.0,
    @SerialName("vote_count") val voteCount: Int = 0,
    val runtime: Int? = null,
    val tagline: String = "",
    val genres: List<Genre> = emptyList(),
    val credits: Credits? = null,
    val reviews: ReviewsResponse? = null,
    val videos: VideosResponse? = null,
    val recommendations: MoviesResponse? = null,
    val similar: MoviesResponse? = null,
    @SerialName("release_dates") val releaseDates: ReleaseDatesResponse? = null,
)

@Serializable
data class ReleaseDatesResponse(
    val results: List<CountryReleaseDates> = emptyList(),
)

@Serializable
data class CountryReleaseDates(
    @SerialName("iso_3166_1") val countryCode: String,
    @SerialName("release_dates") val releaseDates: List<ReleaseDate> = emptyList(),
)

@Serializable
data class ReleaseDate(
    val certification: String = "",
    val type: Int = 0,
)

@Serializable
data class Genre(
    val id: Int,
    val name: String,
)

@Serializable
data class GenresResponse(
    val genres: List<Genre>,
)

@Serializable
data class Credits(
    val cast: List<CastMember> = emptyList(),
    val crew: List<CrewMember> = emptyList(),
)

@Serializable
data class CastMember(
    val id: Int,
    val name: String,
    val character: String = "",
    @SerialName("profile_path") val profilePath: String? = null,
    val order: Int = 0,
)

@Serializable
data class CrewMember(
    val id: Int,
    val name: String,
    val job: String = "",
)

@Serializable
data class ReviewsResponse(
    val results: List<Review> = emptyList(),
)

@Serializable
data class Review(
    val id: String,
    val author: String = "",
    val content: String = "",
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("author_details") val authorDetails: AuthorDetails? = null,
)

@Serializable
data class AuthorDetails(
    val username: String = "",
    val rating: Double? = null,
)

@Serializable
data class VideosResponse(
    val results: List<Video> = emptyList(),
)

@Serializable
data class Video(
    val id: String,
    val key: String,
    val name: String = "",
    val site: String = "",
    val type: String = "",
    val official: Boolean = false,
)

@Serializable
data class WatchProvidersResponse(
    val id: Int,
    @SerialName("results") val results: Map<String, WatchProviderRegion> = emptyMap(),
)

@Serializable
data class WatchProviderRegion(
    val link: String? = null,
    @SerialName("flatrate") val flatrate: List<WatchProvider> = emptyList(),
    val rent: List<WatchProvider> = emptyList(),
    val buy: List<WatchProvider> = emptyList(),
)

@Serializable
data class WatchProvider(
    @SerialName("provider_id") val providerId: Int,
    @SerialName("provider_name") val providerName: String,
    @SerialName("logo_path") val logoPath: String? = null,
    @SerialName("display_priority") val displayPriority: Int = 0,
)
