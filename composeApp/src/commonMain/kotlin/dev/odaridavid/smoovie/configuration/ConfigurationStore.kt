package dev.odaridavid.smoovie.configuration

import org.koin.core.annotation.Single

@Single
class ConfigurationStore {
    var imagesConfiguration: ImagesConfiguration? = null
        private set

    fun save(config: ImagesConfiguration) {
        imagesConfiguration = config
    }
}
