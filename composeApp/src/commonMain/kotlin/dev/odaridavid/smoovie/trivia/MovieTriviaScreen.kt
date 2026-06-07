package dev.odaridavid.smoovie.trivia

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import dev.odaridavid.smoovie.theme.EmptyContent
import dev.odaridavid.smoovie.theme.ErrorContent
import dev.odaridavid.smoovie.theme.SmoovieTheme
import dev.odaridavid.smoovie.trivia.components.TriviaQuestionCard
import dev.odaridavid.smoovie.trivia.components.TriviaResultCard
import dev.odaridavid.smoovie.ui.SetStatusBarIcons
import dev.odaridavid.smoovie.utils.AppError
import dev.odaridavid.smoovie.utils.previewTriviaQuestions
import org.jetbrains.compose.resources.stringResource
import smoovie.composeapp.generated.resources.Res
import smoovie.composeapp.generated.resources.error_movie_detail_failed
import smoovie.composeapp.generated.resources.navigate_back
import smoovie.composeapp.generated.resources.trivia_empty
import smoovie.composeapp.generated.resources.trivia_finish
import smoovie.composeapp.generated.resources.trivia_next
import smoovie.composeapp.generated.resources.trivia_progress
import smoovie.composeapp.generated.resources.trivia_score
import smoovie.composeapp.generated.resources.trivia_title

@Composable
fun MovieTriviaScreen(
    viewModel: MovieTriviaViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    MovieTriviaContent(
        state = state,
        onBack = onBack,
        onSelectAnswer = viewModel::selectAnswer,
        onNext = viewModel::nextQuestion,
        onRestart = viewModel::restart,
        onRetry = viewModel::loadTrivia,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MovieTriviaContent(
    state: TriviaUiState,
    onBack: () -> Unit,
    onSelectAnswer: (Int) -> Unit,
    onNext: () -> Unit,
    onRestart: () -> Unit,
    onRetry: () -> Unit,
) {
    SetStatusBarIcons(useDarkIcons = !isSystemInDarkTheme())
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(Res.string.trivia_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.navigate_back),
                        )
                    }
                },
            )
        },
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (state) {
                is TriviaUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is TriviaUiState.Empty -> {
                    EmptyContent(
                        message = stringResource(Res.string.trivia_empty),
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                is TriviaUiState.Error -> {
                    ErrorContent(
                        error = state.error,
                        title = stringResource(Res.string.error_movie_detail_failed),
                        onRetry = onRetry,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                is TriviaUiState.Content -> {
                    if (state.isFinished) {
                        TriviaResultCard(
                            score = state.score,
                            total = state.totalQuestions,
                            onRestart = onRestart,
                            onDone = onBack,
                            modifier = Modifier.align(Alignment.Center),
                        )
                    } else {
                        QuizBody(
                            state = state,
                            onSelectAnswer = onSelectAnswer,
                            onNext = onNext,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuizBody(
    state: TriviaUiState.Content,
    onSelectAnswer: (Int) -> Unit,
    onNext: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(Res.string.trivia_progress, state.questionNumber, state.totalQuestions),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = stringResource(Res.string.trivia_score, state.score),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        LinearProgressIndicator(
            progress = { state.questionNumber.toFloat() / state.totalQuestions },
            modifier = Modifier.fillMaxWidth(),
        )
        TriviaQuestionCard(
            question = state.currentQuestion,
            selectedOptionIndex = state.selectedOptionIndex,
            onSelectAnswer = onSelectAnswer,
        )
        Button(
            onClick = onNext,
            enabled = state.isAnswered,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                stringResource(
                    if (state.isLastQuestion) Res.string.trivia_finish else Res.string.trivia_next,
                ),
            )
        }
    }
}

// region Previews

@PreviewLightDark
@Composable
private fun MovieTriviaQuestionPreview() {
    SmoovieTheme {
        MovieTriviaContent(
            state = TriviaUiState.Content(questions = previewTriviaQuestions),
            onBack = {},
            onSelectAnswer = {},
            onNext = {},
            onRestart = {},
            onRetry = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun MovieTriviaResultPreview() {
    SmoovieTheme {
        MovieTriviaContent(
            state =
                TriviaUiState.Content(
                    questions = previewTriviaQuestions,
                    currentIndex = previewTriviaQuestions.lastIndex,
                    score = 4,
                    isFinished = true,
                ),
            onBack = {},
            onSelectAnswer = {},
            onNext = {},
            onRestart = {},
            onRetry = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun MovieTriviaErrorPreview() {
    SmoovieTheme {
        MovieTriviaContent(
            state = TriviaUiState.Error(AppError.NetworkError),
            onBack = {},
            onSelectAnswer = {},
            onNext = {},
            onRestart = {},
            onRetry = {},
        )
    }
}

// endregion
