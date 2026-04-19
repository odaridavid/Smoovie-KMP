package dev.odaridavid.smoovie.movies

sealed interface MoviesUiState {
    data object Loading : MoviesUiState

    data class Success(
        val movies: List<MovieUiModel>,
        val isLoadingMore: Boolean = false,
        val hasMorePages: Boolean = false,
    ) : MoviesUiState

    data object Empty : MoviesUiState

    data class Error(
        val message: String,
    ) : MoviesUiState
}
