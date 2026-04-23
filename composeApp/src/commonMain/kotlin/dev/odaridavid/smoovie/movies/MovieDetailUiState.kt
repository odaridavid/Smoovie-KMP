package dev.odaridavid.smoovie.movies

import dev.odaridavid.smoovie.utils.AppError

sealed interface MovieDetailUiState {
    data object Loading : MovieDetailUiState

    data class Success(
        val movieDetail: MovieDetailUiModel,
    ) : MovieDetailUiState

    data class Error(
        val error: AppError,
    ) : MovieDetailUiState
}
