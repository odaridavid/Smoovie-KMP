package dev.odaridavid.smoovie.movies.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.sp
import dev.odaridavid.smoovie.theme.LobsterFontFamily
import dev.odaridavid.smoovie.theme.SmoovieTheme
import org.jetbrains.compose.resources.stringResource
import smoovie.composeapp.generated.resources.Res
import smoovie.composeapp.generated.resources.app_name
import smoovie.composeapp.generated.resources.filter_button_description
import smoovie.composeapp.generated.resources.search_movies_hint

private const val ANIMATION_DURATION_MS = 500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CollapsedToolbar(
    visible: Boolean,
    isFilterActive: Boolean = false,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit = {},
) {
    CenterAlignedTopAppBar(
        title = {
            AnimatedVisibility(
                visible = visible,
                enter =
                    fadeIn(tween(ANIMATION_DURATION_MS)) +
                        slideInHorizontally(tween(ANIMATION_DURATION_MS)) { -it },
                exit =
                    fadeOut(tween(ANIMATION_DURATION_MS)) +
                        slideOutHorizontally(tween(ANIMATION_DURATION_MS)) { -it },
            ) {
                Text(
                    text = stringResource(Res.string.app_name),
                    style =
                        TextStyle(
                            fontFamily = LobsterFontFamily,
                            fontSize = 28.sp,
                        ),
                )
            }
        },
        actions = {
            AnimatedVisibility(
                visible = visible,
                enter =
                    fadeIn(tween(ANIMATION_DURATION_MS)) +
                        slideInHorizontally(tween(ANIMATION_DURATION_MS)) { it },
                exit =
                    fadeOut(tween(ANIMATION_DURATION_MS)) +
                        slideOutHorizontally(tween(ANIMATION_DURATION_MS)) { it },
            ) {
                Row {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(Res.string.search_movies_hint),
                        )
                    }
                    IconButton(onClick = onFilterClick) {
                        BadgedBox(
                            badge = { if (isFilterActive) Badge() },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = stringResource(Res.string.filter_button_description),
                            )
                        }
                    }
                }
            }
        },
    )
}

@PreviewLightDark
@Composable
private fun CollapsedToolbarPreview() {
    SmoovieTheme {
        CollapsedToolbar(visible = true, onSearchClick = {})
    }
}

@PreviewLightDark
@Composable
private fun CollapsedToolbarFilterActivePreview() {
    SmoovieTheme {
        CollapsedToolbar(visible = true, isFilterActive = true, onSearchClick = {}, onFilterClick = {})
    }
}
