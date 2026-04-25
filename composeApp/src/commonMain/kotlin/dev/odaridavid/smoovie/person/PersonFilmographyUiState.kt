package dev.odaridavid.smoovie.person

import dev.odaridavid.smoovie.utils.AppError

sealed interface PersonFilmographyUiState {
    data object Loading : PersonFilmographyUiState

    data class Success(
        val personDetail: PersonDetailUiModel,
    ) : PersonFilmographyUiState

    data class Error(
        val error: AppError,
    ) : PersonFilmographyUiState
}
