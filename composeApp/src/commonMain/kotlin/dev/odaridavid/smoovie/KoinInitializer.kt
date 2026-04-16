package dev.odaridavid.smoovie

import dev.odaridavid.smoovie.configuration.ConfigurationRepositoryImpl
import dev.odaridavid.smoovie.configuration.ConfigurationStore
import dev.odaridavid.smoovie.movies.MovieDetailViewModel
import dev.odaridavid.smoovie.movies.MovieUiMapper
import dev.odaridavid.smoovie.movies.data.MoviesRepositoryImpl
import dev.odaridavid.smoovie.movies.MoviesViewModel
import dev.odaridavid.smoovie.movies.domain.MoviesRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun initKoin() {
    startKoin {
        modules(appModule)
    }
}

private val appModule =
    module {
        single {
            HttpClient {
                install(ContentNegotiation) {
                    json(
                        Json {
                            ignoreUnknownKeys = true
                            coerceInputValues = true
                        },
                    )
                }
                install(Logging) {
                    level = LogLevel.HEADERS
                }
                install(DefaultRequest) {
                    header(HttpHeaders.Authorization, "Bearer $tmdbApiKey")
                }
            }
        }
        single { ConfigurationStore() }
        single<dev.odaridavid.smoovie.configuration.ConfigurationRepository> {
            ConfigurationRepositoryImpl(get())
        }
        single<MoviesRepository> { MoviesRepositoryImpl(get()) }
        single { MovieUiMapper(get()) }
        viewModel { MoviesViewModel(get(), get(), get(), get()) }
        viewModel { (movieId: Int) -> MovieDetailViewModel(movieId, get(), get()) }
    }
