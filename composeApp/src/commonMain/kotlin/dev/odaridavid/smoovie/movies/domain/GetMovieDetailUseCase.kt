package dev.odaridavid.smoovie.movies.domain

import dev.odaridavid.smoovie.configuration.BackdropSize
import dev.odaridavid.smoovie.configuration.ConfigurationStore
import dev.odaridavid.smoovie.movies.MovieDetailUiModel
import dev.odaridavid.smoovie.movies.toDetailUiModel
import dev.odaridavid.smoovie.shared.WatchProviderUiModel
import dev.odaridavid.smoovie.shared.data.WatchProvider
import dev.odaridavid.smoovie.shared.data.WatchProviderRegion
import dev.odaridavid.smoovie.shared.data.WatchProvidersResponse
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
            val keywordsDeferred = async { runCatching { repository.getMovieKeywords(movieId) }.getOrNull() }

            val detail = detailDeferred.await()
            val regionData = resolveRegionData(providersDeferred.await())
            val keywords = keywordsDeferred.await()?.keywords?.take(MAX_KEYWORDS)?.map { it.name } ?: emptyList()

            val streamingProviders = mapProviders(regionData?.flatrate.orEmpty())
            val streamingIds = streamingProviders.map { it.name }.toSet()
            val rentBuyProviders =
                mapProviders(
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
                keywords = keywords,
            )
        }

    private companion object {
        const val MAX_KEYWORDS = 3
    }

    private fun resolveRegionData(response: WatchProvidersResponse?): WatchProviderRegion? =
        response?.results?.let { it["DE"] ?: it["US"] ?: it.values.firstOrNull() }

    private fun mapProviders(providers: List<WatchProvider>): List<WatchProviderUiModel> =
        providers
            .sortedBy { it.displayPriority }
            .map { WatchProviderUiModel(name = it.providerName, logoUrl = configurationStore.logoUrl(it.logoPath)) }
}
