package dev.odaridavid.smoovie.trivia.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import dev.odaridavid.smoovie.theme.SmoovieTheme
import org.jetbrains.compose.resources.stringResource
import smoovie.composeapp.generated.resources.Res
import smoovie.composeapp.generated.resources.trivia_done
import smoovie.composeapp.generated.resources.trivia_play_again
import smoovie.composeapp.generated.resources.trivia_result_score
import smoovie.composeapp.generated.resources.trivia_result_title

@Composable
internal fun TriviaResultCard(
    score: Int,
    total: Int,
    onRestart: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(Res.string.trivia_result_title),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(Res.string.trivia_result_score, score, total),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )
        Button(onClick = onRestart, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(Res.string.trivia_play_again))
        }
        OutlinedButton(onClick = onDone, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(Res.string.trivia_done))
        }
    }
}

// region Previews

@PreviewLightDark
@Composable
private fun TriviaResultCardPreview() {
    SmoovieTheme {
        TriviaResultCard(score = 4, total = 5, onRestart = {}, onDone = {})
    }
}

// endregion
