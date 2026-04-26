package dev.odaridavid.smoovie.shared.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
data class Keyword(
    val id: Int,
    val name: String,
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
