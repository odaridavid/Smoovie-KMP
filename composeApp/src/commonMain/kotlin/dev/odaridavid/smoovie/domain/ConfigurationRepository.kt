package dev.odaridavid.smoovie.domain

import dev.odaridavid.smoovie.data.model.ImagesConfiguration

interface ConfigurationRepository {
    suspend fun getImagesConfiguration(): ImagesConfiguration
}
