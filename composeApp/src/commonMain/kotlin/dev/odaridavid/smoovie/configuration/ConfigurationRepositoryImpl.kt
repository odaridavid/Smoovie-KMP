package dev.odaridavid.smoovie.configuration

import dev.odaridavid.smoovie.TMDB_BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.koin.core.annotation.Single

@Single
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
