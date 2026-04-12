package dev.odaridavid.smoovie.configuration

interface ConfigurationRepository {
    suspend fun getImagesConfiguration(): ImagesConfiguration
}
