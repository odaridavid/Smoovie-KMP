package dev.odaridavid.smoovie.configuration

class ConfigurationStore {
    private var imagesConfiguration: ImagesConfiguration? = null

    fun save(config: ImagesConfiguration) {
        imagesConfiguration = config
    }

    fun backdropUrl(
        path: String?,
        size: BackdropSize = BackdropSize.MEDIUM,
    ): String? = path?.let { "${imagesConfiguration?.secureBaseUrl}${size.apiValue}$it" }

    fun posterUrl(
        path: String?,
        size: PosterSize = PosterSize.MEDIUM,
    ): String? = path?.let { "${imagesConfiguration?.secureBaseUrl}${size.apiValue}$it" }
}
