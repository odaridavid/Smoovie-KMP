package dev.odaridavid.smoovie.data

import dev.odaridavid.smoovie.data.model.ImagesConfiguration

interface ConfigurationRepository {
    suspend fun getImagesConfiguration(): ImagesConfiguration
}
