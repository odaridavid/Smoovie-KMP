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
    @SerialName("backdrop_sizes") val backdropSizes: List<String>,
    @SerialName("poster_sizes") val posterSizes: List<String>,
)
