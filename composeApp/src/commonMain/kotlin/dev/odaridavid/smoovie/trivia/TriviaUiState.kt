package dev.odaridavid.smoovie.trivia

import dev.odaridavid.smoovie.utils.AppError

sealed interface TriviaUiState {
    data object Loading : TriviaUiState

    data object Empty : TriviaUiState

    data class Content(
        val questions: List<TriviaQuestionUiModel>,
        val currentIndex: Int = 0,
        val selectedOptionIndex: Int? = null,
        val score: Int = 0,
        val isFinished: Boolean = false,
    ) : TriviaUiState {
        val currentQuestion: TriviaQuestionUiModel get() = questions[currentIndex]
        val questionNumber: Int get() = currentIndex + 1
        val totalQuestions: Int get() = questions.size
        val isAnswered: Boolean get() = selectedOptionIndex != null
        val isLastQuestion: Boolean get() = currentIndex == questions.lastIndex
    }

    data class Error(
        val error: AppError,
    ) : TriviaUiState
}
