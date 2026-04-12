package dev.odaridavid.smoovie.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import smoovie.composeapp.generated.resources.Res
import smoovie.composeapp.generated.resources.action_retry
import smoovie.composeapp.generated.resources.error_movies_failed

@Composable
internal fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(Res.string.error_movies_failed),
            style = MaterialTheme.typography.titleMedium,
        )
        Text(text = message, style = MaterialTheme.typography.bodySmall)
        Button(onClick = onRetry) { Text(stringResource(Res.string.action_retry)) }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ErrorContentPreview() {
    SmoovieTheme {
        ErrorContent(message = "No internet connection", onRetry = {})
    }
}
