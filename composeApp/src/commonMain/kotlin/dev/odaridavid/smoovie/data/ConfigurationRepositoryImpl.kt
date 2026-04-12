package dev.odaridavid.smoovie.data

import dev.odaridavid.smoovie.data.model.ImagesConfiguration
import dev.odaridavid.smoovie.data.model.Configuration
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class ConfigurationRepositoryImpl(
    private val client: HttpClient,
) : ConfigurationRepository {
    override suspend fun getImagesConfiguration(): ImagesConfiguration =
        client
            .get(CONFIGURATION_PATH)
            .body<Configuration>()
            .images

    companion object {
        private const val CONFIGURATION_PATH = "$TMDB_BASE_URL/configuration"
    }
}
