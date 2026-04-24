package dev.odaridavid.smoovie.shows.domain

import dev.odaridavid.smoovie.configuration.BackdropSize
import dev.odaridavid.smoovie.configuration.ConfigurationStore
import dev.odaridavid.smoovie.shows.TvShowDetailUiModel
import dev.odaridavid.smoovie.shows.toDetailUiModel

class GetTvShowDetailUseCase(
    private val repository: TvShowsRepository,
    private val configurationStore: ConfigurationStore,
) {
    suspend operator fun invoke(
        tvShowId: Int,
        presentLabel: String,
    ): TvShowDetailUiModel =
        repository.getTvShowDetail(tvShowId).let { detail ->
            detail.toDetailUiModel(
                backdropUrl = configurationStore.backdropUrl(detail.backdropPath, BackdropSize.LARGE),
                posterUrl = configurationStore.posterUrl(detail.posterPath),
                profileUrlResolver = { configurationStore.profileUrl(it) },
                seasonPosterResolver = { configurationStore.posterUrl(it) },
                similarBackdropResolver = { configurationStore.backdropUrl(it) },
                similarPosterResolver = { configurationStore.posterUrl(it) },
                presentLabel = presentLabel,
            )
        }
}
