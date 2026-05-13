package dev.odaridavid.smoovie

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import dev.odaridavid.smoovie.configuration.ConfigurationRepository
import dev.odaridavid.smoovie.configuration.ConfigurationRepositoryImpl
import dev.odaridavid.smoovie.configuration.ConfigurationStore
import dev.odaridavid.smoovie.configuration.LoadConfigurationUseCase
import dev.odaridavid.smoovie.filter.FilterPreferencesStore
import dev.odaridavid.smoovie.filter.FilterPreferencesStoreImpl
import dev.odaridavid.smoovie.movies.MovieDetailViewModel
import dev.odaridavid.smoovie.movies.MovieUiMapper
import dev.odaridavid.smoovie.movies.MoviesViewModel
import dev.odaridavid.smoovie.movies.data.MoviesRepositoryImpl
import dev.odaridavid.smoovie.movies.domain.DiscoverMoviesUseCase
import dev.odaridavid.smoovie.movies.domain.GetGenresUseCase
import dev.odaridavid.smoovie.movies.domain.GetMovieDetailUseCase
import dev.odaridavid.smoovie.movies.domain.GetPopularMoviesUseCase
import dev.odaridavid.smoovie.movies.domain.GetTrendingMoviesUseCase
import dev.odaridavid.smoovie.movies.domain.MoviesRepository
import dev.odaridavid.smoovie.movies.domain.SearchMoviesUseCase
import dev.odaridavid.smoovie.observability.Logger
import dev.odaridavid.smoovie.observability.NapierKtorLogger
import dev.odaridavid.smoovie.observability.NapierLogger
import dev.odaridavid.smoovie.observability.setCrashReportingEnabled
import dev.odaridavid.smoovie.person.PersonDetailViewModel
import dev.odaridavid.smoovie.person.PersonFilmographyViewModel
import dev.odaridavid.smoovie.person.data.PersonRepositoryImpl
import dev.odaridavid.smoovie.person.domain.GetPersonDetailUseCase
import dev.odaridavid.smoovie.person.domain.PersonRepository
import dev.odaridavid.smoovie.security.AppCheckHeader
import dev.odaridavid.smoovie.settings.CrashReportingConsentViewModel
import dev.odaridavid.smoovie.settings.SettingsPreferencesStore
import dev.odaridavid.smoovie.settings.SettingsPreferencesStoreImpl
import dev.odaridavid.smoovie.settings.SettingsViewModel
import dev.odaridavid.smoovie.shows.SeasonDetailViewModel
import dev.odaridavid.smoovie.shows.ShowsViewModel
import dev.odaridavid.smoovie.shows.TvShowDetailViewModel
import dev.odaridavid.smoovie.shows.TvShowUiMapper
import dev.odaridavid.smoovie.shows.data.TvShowsRepositoryImpl
import dev.odaridavid.smoovie.shows.domain.DiscoverTvShowsUseCase
import dev.odaridavid.smoovie.shows.domain.GetPopularTvShowsUseCase
import dev.odaridavid.smoovie.shows.domain.GetSeasonDetailUseCase
import dev.odaridavid.smoovie.shows.domain.GetTvGenresUseCase
import dev.odaridavid.smoovie.shows.domain.GetTvShowDetailUseCase
import dev.odaridavid.smoovie.shows.domain.SearchTvShowsUseCase
import dev.odaridavid.smoovie.shows.domain.TvShowsRepository
import dev.odaridavid.smoovie.storage.DatabaseBuilderFactory
import dev.odaridavid.smoovie.storage.MIGRATION_1_2
import dev.odaridavid.smoovie.storage.SmoovieDatabase
import dev.odaridavid.smoovie.watchlist.WatchlistViewModel
import dev.odaridavid.smoovie.watchlist.data.WatchlistRepositoryImpl
import dev.odaridavid.smoovie.watchlist.domain.ObserveIsInWatchlistUseCase
import dev.odaridavid.smoovie.watchlist.domain.ObserveWatchlistUseCase
import dev.odaridavid.smoovie.watchlist.domain.RemoveFromWatchlistUseCase
import dev.odaridavid.smoovie.watchlist.domain.ToggleWatchlistUseCase
import dev.odaridavid.smoovie.watchlist.domain.WatchlistRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun initKoin(setup: KoinApplication.() -> Unit = {}) {
    val koinApp =
        startKoin {
            setup()
            modules(appModule, platformModule)
        }
    val settingsPreferencesStore = koinApp.koin.get<SettingsPreferencesStore>()
    setCrashReportingEnabled(settingsPreferencesStore.crashReportingEnabled.value)
}

private val appModule =
    module {
        single<Logger> { NapierLogger() }
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
                    logger = NapierKtorLogger(get())
                    level = LogLevel.HEADERS
                }
                install(AppCheckHeader)
            }
        }
        single { ConfigurationStore() }
        single<ConfigurationRepository> { ConfigurationRepositoryImpl(get()) }
        single<SettingsPreferencesStore> { SettingsPreferencesStoreImpl(get()) }
        single<MoviesRepository> { MoviesRepositoryImpl(get(), get()) }
        single<PersonRepository> { PersonRepositoryImpl(get()) }
        single<TvShowsRepository> { TvShowsRepositoryImpl(get(), get()) }
        single<SmoovieDatabase> {
            get<DatabaseBuilderFactory>()
                .create()
                .addMigrations(MIGRATION_1_2)
                .setDriver(BundledSQLiteDriver())
                .setQueryCoroutineContext(Dispatchers.Default)
                .build()
        }
        single { get<SmoovieDatabase>().watchlistDao() }
        single<WatchlistRepository> { WatchlistRepositoryImpl(get()) }

        single<FilterPreferencesStore> { FilterPreferencesStoreImpl(get()) }

        single { MovieUiMapper(get()) }
        single { TvShowUiMapper(get()) }

        single { LoadConfigurationUseCase(get(), get()) }
        single { GetPopularMoviesUseCase(get(), get()) }
        single { SearchMoviesUseCase(get(), get()) }
        single { DiscoverMoviesUseCase(get(), get()) }
        single { GetGenresUseCase(get()) }
        single { GetMovieDetailUseCase(get(), get(), get()) }
        single { GetTrendingMoviesUseCase(get(), get()) }
        single { GetPopularTvShowsUseCase(get(), get()) }
        single { SearchTvShowsUseCase(get(), get()) }
        single { DiscoverTvShowsUseCase(get(), get()) }
        single { GetTvGenresUseCase(get()) }
        single { GetTvShowDetailUseCase(get(), get(), get()) }
        single { GetSeasonDetailUseCase(get(), get()) }
        single { GetPersonDetailUseCase(get(), get()) }
        single { ObserveIsInWatchlistUseCase(get()) }
        single { ObserveWatchlistUseCase(get()) }
        single { ToggleWatchlistUseCase(get()) }
        single { RemoveFromWatchlistUseCase(get()) }

        viewModel {
            MoviesViewModel(
                getPopularMovies = get(),
                getTrendingMovies = get(),
                searchMovies = get(),
                discoverMovies = get(),
                getGenres = get(),
                loadConfiguration = get(),
                filterPreferencesStore = get(),
                settingsPreferencesStore = get(),
            )
        }
        viewModel {
            ShowsViewModel(
                getPopularTvShows = get(),
                searchTvShows = get(),
                discoverTvShows = get(),
                getTvGenres = get(),
                loadConfiguration = get(),
                filterPreferencesStore = get(),
                settingsPreferencesStore = get(),
            )
        }
        viewModel { SettingsViewModel(get()) }
        viewModel { CrashReportingConsentViewModel(get()) }
        viewModel { (tvShowId: Int, presentLabel: String) ->
            TvShowDetailViewModel(
                tvShowId = tvShowId,
                presentLabel = presentLabel,
                getTvShowDetail = get(),
                observeIsInWatchlist = get(),
                toggleWatchlistUseCase = get(),
            )
        }
        viewModel { (tvShowId: Int, seasonNumber: Int) ->
            SeasonDetailViewModel(
                tvShowId = tvShowId,
                seasonNumber = seasonNumber,
                getSeasonDetail = get(),
            )
        }
        viewModel { (movieId: Int) ->
            MovieDetailViewModel(
                observeIsInWatchlist = get(),
                movieId = movieId,
                getMovieDetail = get(),
                toggleWatchlistUseCase = get(),
            )
        }
        viewModel { (personId: Int) -> PersonDetailViewModel(personId, get()) }
        viewModel { (personId: Int) -> PersonFilmographyViewModel(personId, get()) }
        viewModel { WatchlistViewModel(get(), get(), get()) }
    }
