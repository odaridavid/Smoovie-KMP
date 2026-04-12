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
            backdropSizes = listOf("w300", "w780", "w1280", "original"),
            posterSizes = listOf("w92", "w154", "w185", "w342", "w500", "w780", "original"),
        )
    }
}
