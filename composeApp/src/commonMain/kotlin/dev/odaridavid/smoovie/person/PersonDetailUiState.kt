package dev.odaridavid.smoovie.person

import dev.odaridavid.smoovie.utils.AppError

sealed interface PersonDetailUiState {
    data object Loading : PersonDetailUiState

    data class Success(
        val personDetail: PersonDetailUiModel,
    ) : PersonDetailUiState

    data class Error(
        val error: AppError,
    ) : PersonDetailUiState
}
