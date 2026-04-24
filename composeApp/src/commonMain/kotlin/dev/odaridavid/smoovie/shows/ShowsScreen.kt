package dev.odaridavid.smoovie.shows

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tv
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import dev.odaridavid.smoovie.theme.EmptyContent
import dev.odaridavid.smoovie.theme.SmoovieTheme
import org.jetbrains.compose.resources.stringResource
import smoovie.composeapp.generated.resources.Res
import smoovie.composeapp.generated.resources.tv_shows_coming_soon

@Composable
fun ShowsScreen() {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .statusBarsPadding(),
    ) {
        EmptyContent(
            message = stringResource(Res.string.tv_shows_coming_soon),
            imageVector = Icons.Default.Tv,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@PreviewLightDark
@Composable
private fun ShowsScreenPreview() {
    SmoovieTheme {
        ShowsScreen()
    }
}