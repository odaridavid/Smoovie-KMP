package dev.odaridavid.smoovie.shows.domain

import dev.odaridavid.smoovie.shows.TvShowUiModel

data class TvShowsPage(
    val tvShows: List<TvShowUiModel>,
    val page: Int,
    val totalPages: Int,
)
