package dev.odaridavid.smoovie.shows

import dev.odaridavid.smoovie.utils.AppError

sealed interface TvShowDetailUiState {
    data object Loading : TvShowDetailUiState

    data class Success(
        val tvShowDetail: TvShowDetailUiModel,
    ) : TvShowDetailUiState

    data class Error(
        val error: AppError,
    ) : TvShowDetailUiState
}
