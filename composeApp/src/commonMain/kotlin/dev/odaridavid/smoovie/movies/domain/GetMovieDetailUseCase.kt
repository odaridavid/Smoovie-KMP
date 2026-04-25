package dev.odaridavid.smoovie.movies.domain

import dev.odaridavid.smoovie.configuration.BackdropSize
import dev.odaridavid.smoovie.configuration.ConfigurationStore
import dev.odaridavid.smoovie.movies.MovieDetailUiModel
import dev.odaridavid.smoovie.movies.WatchProviderUiModel
import dev.odaridavid.smoovie.movies.toDetailUiModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class GetMovieDetailUseCase(
    private val repository: MoviesRepository,
    private val configurationStore: ConfigurationStore,
) {
    suspend operator fun invoke(movieId: Int): MovieDetailUiModel =
        coroutineScope {
            val detailDeferred = async { repository.getMovieDetail(movieId) }
            val providersDeferred = async { runCatching { repository.getWatchProviders(movieId) }.getOrNull() }

            val detail = detailDeferred.await()
            val providersResponse = providersDeferred.await()

            val regionData = providersResponse?.results?.let { it["DE"] ?: it["US"] ?: it.values.firstOrNull() }

            fun mapProviders(providers: List<dev.odaridavid.smoovie.movies.data.WatchProvider>) =
                providers.sortedBy { it.displayPriority }
                    .map { WatchProviderUiModel(name = it.providerName, logoUrl = configurationStore.logoUrl(it.logoPath)) }

            val streamingProviders = mapProviders(regionData?.flatrate.orEmpty())
            val streamingIds = streamingProviders.map { it.name }.toSet()
            val rentBuyProviders = mapProviders(
                (regionData?.rent.orEmpty() + regionData?.buy.orEmpty())
                    .distinctBy { it.providerId }
                    .filter { it.providerName !in streamingIds },
            )

            detail.toDetailUiModel(
                backdropUrl = configurationStore.backdropUrl(detail.backdropPath, BackdropSize.LARGE),
                posterUrl = configurationStore.posterUrl(detail.posterPath),
                profileUrlResolver = { configurationStore.profileUrl(it) },
                movieBackdropUrlResolver = { configurationStore.backdropUrl(it) },
                moviePosterUrlResolver = { configurationStore.posterUrl(it) },
                streamingProviders = streamingProviders,
                rentBuyProviders = rentBuyProviders,
                watchProvidersLink = regionData?.link,
            )
        }
}
