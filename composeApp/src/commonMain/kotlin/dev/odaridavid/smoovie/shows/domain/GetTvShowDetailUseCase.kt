package dev.odaridavid.smoovie.shows.domain

import dev.odaridavid.smoovie.configuration.BackdropSize
import dev.odaridavid.smoovie.configuration.ConfigurationStore
import dev.odaridavid.smoovie.movies.WatchProviderUiModel
import dev.odaridavid.smoovie.movies.data.WatchProvider
import dev.odaridavid.smoovie.movies.data.WatchProviderRegion
import dev.odaridavid.smoovie.movies.data.WatchProvidersResponse
import dev.odaridavid.smoovie.shows.TvShowDetailUiModel
import dev.odaridavid.smoovie.shows.toDetailUiModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class GetTvShowDetailUseCase(
    private val repository: TvShowsRepository,
    private val configurationStore: ConfigurationStore,
) {
    suspend operator fun invoke(
        tvShowId: Int,
        presentLabel: String,
    ): TvShowDetailUiModel =
        coroutineScope {
            val detailDeferred = async { repository.getTvShowDetail(tvShowId) }
            val providersDeferred = async { runCatching { repository.getWatchProviders(tvShowId) }.getOrNull() }
            val keywordsDeferred = async { runCatching { repository.getKeywords(tvShowId) }.getOrNull() }

            val detail = detailDeferred.await()
            val regionData = resolveRegionData(providersDeferred.await())
            val keywords = keywordsDeferred.await()?.results?.take(MAX_KEYWORDS)?.map { it.name } ?: emptyList()

            val streamingProviders = mapProviders(regionData?.flatrate.orEmpty())
            val streamingNames = streamingProviders.map { it.name }.toSet()
            val rentBuyProviders =
                mapProviders(
                    (regionData?.rent.orEmpty() + regionData?.buy.orEmpty())
                        .distinctBy { it.providerId }
                        .filter { it.providerName !in streamingNames },
                )

            detail.toDetailUiModel(
                backdropUrl = configurationStore.backdropUrl(detail.backdropPath, BackdropSize.LARGE),
                posterUrl = configurationStore.posterUrl(detail.posterPath),
                profileUrlResolver = { configurationStore.profileUrl(it) },
                seasonPosterResolver = { configurationStore.posterUrl(it) },
                similarBackdropResolver = { configurationStore.backdropUrl(it) },
                similarPosterResolver = { configurationStore.posterUrl(it) },
                presentLabel = presentLabel,
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
