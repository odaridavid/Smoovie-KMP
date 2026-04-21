package dev.odaridavid.smoovie.person

sealed interface PersonDetailUiState {
    data object Loading : PersonDetailUiState

    data class Success(
        val personDetail: PersonDetailUiModel,
    ) : PersonDetailUiState

    data class Error(
        val message: String,
    ) : PersonDetailUiState
}
