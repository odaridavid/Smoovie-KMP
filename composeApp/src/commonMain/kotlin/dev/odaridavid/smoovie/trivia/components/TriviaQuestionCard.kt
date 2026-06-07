package dev.odaridavid.smoovie.trivia.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import dev.odaridavid.smoovie.theme.SmoovieTheme
import dev.odaridavid.smoovie.trivia.TriviaQuestionKind
import dev.odaridavid.smoovie.trivia.TriviaQuestionUiModel
import dev.odaridavid.smoovie.utils.previewTriviaQuestions
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import smoovie.composeapp.generated.resources.Res
import smoovie.composeapp.generated.resources.trivia_q_cast
import smoovie.composeapp.generated.resources.trivia_q_director
import smoovie.composeapp.generated.resources.trivia_q_genre
import smoovie.composeapp.generated.resources.trivia_q_runtime
import smoovie.composeapp.generated.resources.trivia_q_year

@Composable
internal fun TriviaQuestionCard(
    question: TriviaQuestionUiModel,
    selectedOptionIndex: Int?,
    onSelectAnswer: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(question.kind.promptResource(), question.subject),
            style = MaterialTheme.typography.titleLarge,
        )
        question.options.forEachIndexed { index, option ->
            AnswerOption(
                label = option,
                state = optionState(index, question.correctIndex, selectedOptionIndex),
                enabled = selectedOptionIndex == null,
                onClick = { onSelectAnswer(index) },
            )
        }
    }
}

private enum class OptionState { DEFAULT, CORRECT, INCORRECT }

private fun optionState(
    index: Int,
    correctIndex: Int,
    selectedOptionIndex: Int?,
): OptionState =
    when {
        selectedOptionIndex == null -> OptionState.DEFAULT
        index == correctIndex -> OptionState.CORRECT
        index == selectedOptionIndex -> OptionState.INCORRECT
        else -> OptionState.DEFAULT
    }

@Composable
private fun AnswerOption(
    label: String,
    state: OptionState,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val transition = updateTransition(targetState = state, label = "answerOption")
    val containerColor by transition.animateColor(label = "container") { it.containerColor() }
    val contentColor by transition.animateColor(label = "content") { it.contentColor() }
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .drawBehind { drawRect(containerColor) }
                .selectable(selected = state != OptionState.DEFAULT, enabled = enabled, onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = contentColor,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(end = 12.dp),
        )
        when (state) {
            OptionState.CORRECT -> Icon(Icons.Default.Check, contentDescription = null, tint = contentColor)
            OptionState.INCORRECT -> Icon(Icons.Default.Close, contentDescription = null, tint = contentColor)
            OptionState.DEFAULT -> Icon(Icons.Default.Check, contentDescription = null, tint = Color.Transparent)
        }
    }
}

@Composable
private fun OptionState.containerColor(): Color =
    when (this) {
        OptionState.DEFAULT -> MaterialTheme.colorScheme.surfaceVariant
        OptionState.CORRECT -> MaterialTheme.colorScheme.tertiaryContainer
        OptionState.INCORRECT -> MaterialTheme.colorScheme.errorContainer
    }

@Composable
private fun OptionState.contentColor(): Color =
    when (this) {
        OptionState.DEFAULT -> MaterialTheme.colorScheme.onSurfaceVariant
        OptionState.CORRECT -> MaterialTheme.colorScheme.onTertiaryContainer
        OptionState.INCORRECT -> MaterialTheme.colorScheme.onErrorContainer
    }

private fun TriviaQuestionKind.promptResource(): StringResource =
    when (this) {
        TriviaQuestionKind.RELEASE_YEAR -> Res.string.trivia_q_year
        TriviaQuestionKind.RUNTIME -> Res.string.trivia_q_runtime
        TriviaQuestionKind.GENRE -> Res.string.trivia_q_genre
        TriviaQuestionKind.DIRECTOR -> Res.string.trivia_q_director
        TriviaQuestionKind.CAST -> Res.string.trivia_q_cast
    }

// region Previews

@PreviewLightDark
@Composable
private fun TriviaQuestionCardUnansweredPreview() {
    SmoovieTheme {
        TriviaQuestionCard(
            question = previewTriviaQuestions[0],
            selectedOptionIndex = null,
            onSelectAnswer = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@PreviewLightDark
@Composable
private fun TriviaQuestionCardAnsweredPreview() {
    SmoovieTheme {
        val question = previewTriviaQuestions[0]
        TriviaQuestionCard(
            question = question,
            selectedOptionIndex = (question.correctIndex + 1) % question.options.size,
            onSelectAnswer = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

// endregion
