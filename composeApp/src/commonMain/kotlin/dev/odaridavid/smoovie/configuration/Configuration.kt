package dev.odaridavid.smoovie.configuration

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Configuration(
    val images: ImagesConfiguration,
)

@Serializable
data class ImagesConfiguration(
    @SerialName("base_url") val baseUrl: String,
    @SerialName("secure_base_url") val secureBaseUrl: String,
) {
}

enum class BackdropSize(
    val apiValue: String,
) {
    SMALL("w300"),
    MEDIUM("w780"),
    LARGE("w1280"),
    ORIGINAL("original"),
}

enum class PosterSize(
    val apiValue: String,
) {
    TINY("w92"),
    SMALL("w154"),
    COMPACT("w185"),
    MEDIUM("w342"),
    LARGE("w500"),
    XLARGE("w780"),
    ORIGINAL("original"),
}
