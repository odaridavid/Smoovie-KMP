package dev.odaridavid.smoovie.trivia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.odaridavid.smoovie.trivia.domain.GenerateMovieTriviaUseCase
import dev.odaridavid.smoovie.utils.toAppError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MovieTriviaViewModel(
    private val movieId: Int,
    private val generateTrivia: GenerateMovieTriviaUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<TriviaUiState>(TriviaUiState.Loading)
    val uiState: StateFlow<TriviaUiState> = _uiState.asStateFlow()

    init {
        loadTrivia()
    }

    fun loadTrivia() {
        viewModelScope.launch {
            _uiState.value = TriviaUiState.Loading
            try {
                val questions = generateTrivia(movieId)
                _uiState.value =
                    if (questions.isEmpty()) {
                        TriviaUiState.Empty
                    } else {
                        TriviaUiState.Content(questions = questions)
                    }
            } catch (e: Exception) {
                _uiState.value = TriviaUiState.Error(e.toAppError())
            }
        }
    }

    fun selectAnswer(optionIndex: Int) {
        _uiState.update { state ->
            if (state !is TriviaUiState.Content || state.isAnswered) {
                state
            } else {
                val isCorrect = optionIndex == state.currentQuestion.correctIndex
                state.copy(
                    selectedOptionIndex = optionIndex,
                    score = if (isCorrect) state.score + 1 else state.score,
                )
            }
        }
    }

    fun nextQuestion() {
        _uiState.update { state ->
            if (state !is TriviaUiState.Content || !state.isAnswered) {
                state
            } else if (state.isLastQuestion) {
                state.copy(isFinished = true)
            } else {
                state.copy(
                    currentIndex = state.currentIndex + 1,
                    selectedOptionIndex = null,
                )
            }
        }
    }

    fun restart() {
        loadTrivia()
    }
}
