package dev.odaridavid.smoovie.watchlist

import dev.odaridavid.smoovie.movies.MovieUiModel

sealed interface WatchlistUiState {
    data object Loading : WatchlistUiState

    data object Empty : WatchlistUiState

    data class Loaded(
        val movies: List<MovieUiModel>,
    ) : WatchlistUiState
}
