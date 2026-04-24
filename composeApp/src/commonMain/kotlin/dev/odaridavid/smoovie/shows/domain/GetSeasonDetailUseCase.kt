package dev.odaridavid.smoovie.shows.domain

import dev.odaridavid.smoovie.configuration.BackdropSize
import dev.odaridavid.smoovie.configuration.ConfigurationStore
import dev.odaridavid.smoovie.shows.SeasonDetailUiModel
import dev.odaridavid.smoovie.shows.toUiModel

class GetSeasonDetailUseCase(
    private val repository: TvShowsRepository,
    private val configurationStore: ConfigurationStore,
) {
    suspend operator fun invoke(
        tvShowId: Int,
        seasonNumber: Int,
    ): SeasonDetailUiModel =
        repository.getSeasonDetail(tvShowId, seasonNumber).let { detail ->
            detail.toUiModel(
                posterUrl = configurationStore.posterUrl(detail.posterPath),
                stillUrlResolver = { configurationStore.backdropUrl(it, BackdropSize.SMALL) },
            )
        }
}
