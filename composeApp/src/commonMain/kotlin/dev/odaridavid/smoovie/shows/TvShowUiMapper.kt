package dev.odaridavid.smoovie.shows

import dev.odaridavid.smoovie.configuration.ConfigurationStore
import dev.odaridavid.smoovie.shows.data.TvShow

class TvShowUiMapper(
    private val configurationStore: ConfigurationStore,
) {
    fun toUiModels(tvShows: List<TvShow>): List<TvShowUiModel> =
        tvShows.map { tvShow ->
            tvShow.toUiModel(
                backdropUrl = configurationStore.backdropUrl(tvShow.backdropPath),
                posterUrl = configurationStore.posterUrl(tvShow.posterPath),
            )
        }
}
