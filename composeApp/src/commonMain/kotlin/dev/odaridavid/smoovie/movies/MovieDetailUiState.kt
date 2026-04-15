package dev.odaridavid.smoovie.movies

sealed interface MovieDetailUiState {
    data object Loading : MovieDetailUiState

    data class Success(
        val movieDetail: MovieDetailUiModel,
    ) : MovieDetailUiState

    data class Error(
        val message: String,
    ) : MovieDetailUiState
}
