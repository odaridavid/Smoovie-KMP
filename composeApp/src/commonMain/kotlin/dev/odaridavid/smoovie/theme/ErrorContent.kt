package dev.odaridavid.smoovie.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import smoovie.composeapp.generated.resources.Res
import dev.odaridavid.smoovie.utils.AppError
import smoovie.composeapp.generated.resources.action_retry
import smoovie.composeapp.generated.resources.error_movies_failed
import smoovie.composeapp.generated.resources.error_network
import smoovie.composeapp.generated.resources.error_not_found
import smoovie.composeapp.generated.resources.error_server
import smoovie.composeapp.generated.resources.error_unauthorized
import smoovie.composeapp.generated.resources.error_unknown

private val ICON_BADGE_SIZE = 112.dp
private val ICON_SIZE = 56.dp

@Composable
internal fun ErrorContent(
    error: AppError,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(Res.string.error_movies_failed),
) {
    val message =
        stringResource(
            when (error) {
                AppError.NetworkError -> Res.string.error_network
                AppError.ServerError -> Res.string.error_server
                AppError.NotFound -> Res.string.error_not_found
                AppError.Unauthorized -> Res.string.error_unauthorized
                AppError.Unknown -> Res.string.error_unknown
            },
        )
    Column(
        modifier = modifier.padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(ICON_BADGE_SIZE)
                    .background(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = CircleShape,
                    ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.CloudOff,
                contentDescription = null,
                modifier = Modifier.size(ICON_SIZE),
                tint = MaterialTheme.colorScheme.onErrorContainer,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(8.dp))
        FilledTonalButton(onClick = onRetry) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize),
            )
            Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
            Text(stringResource(Res.string.action_retry))
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ErrorContentPreview() {
    SmoovieTheme {
        Scaffold {
            Box(modifier = Modifier.fillMaxSize()) {
                ErrorContent(
                    error = AppError.NetworkError,
                    onRetry = {},
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
    }
}
