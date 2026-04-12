package dev.odaridavid.smoovie

import dev.odaridavid.smoovie.configuration.ConfigurationRepository
import dev.odaridavid.smoovie.configuration.ImagesConfiguration

class FakeConfigurationRepository(
    var error: Exception? = null,
) : ConfigurationRepository {
    override suspend fun getImagesConfiguration(): ImagesConfiguration {
        error?.let { throw it }
        return ImagesConfiguration(
            baseUrl = "http://image.tmdb.org/t/p/",
            secureBaseUrl = "https://image.tmdb.org/t/p/",
        )
    }
}
