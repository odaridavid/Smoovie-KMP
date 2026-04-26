package dev.odaridavid.smoovie.shared

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
