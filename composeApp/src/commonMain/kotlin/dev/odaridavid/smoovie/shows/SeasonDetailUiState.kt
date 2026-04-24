package dev.odaridavid.smoovie.shows

import dev.odaridavid.smoovie.utils.AppError

sealed interface SeasonDetailUiState {
    data object Loading : SeasonDetailUiState

    data class Success(
        val seasonDetail: SeasonDetailUiModel,
    ) : SeasonDetailUiState

    data class Error(
        val error: AppError,
    ) : SeasonDetailUiState
}
