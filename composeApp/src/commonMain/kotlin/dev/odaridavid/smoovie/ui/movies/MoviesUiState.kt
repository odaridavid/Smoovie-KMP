package dev.odaridavid.smoovie.ui.movies

import dev.odaridavid.smoovie.data.model.Movie

sealed interface MoviesUiState {
    data object Loading : MoviesUiState

    data class Success(
        val movies: List<Movie>,
    ) : MoviesUiState

    data class Error(
        val message: String,
    ) : MoviesUiState
}
