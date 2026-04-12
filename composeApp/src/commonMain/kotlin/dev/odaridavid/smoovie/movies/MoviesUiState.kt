package dev.odaridavid.smoovie.movies

sealed interface MoviesUiState {
    data object Loading : MoviesUiState

    data class Success(
        val movies: List<Movie>,
    ) : MoviesUiState

    data class Error(
        val message: String,
    ) : MoviesUiState
}
