package dev.odaridavid.smoovie.watchlist

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.odaridavid.smoovie.movies.MovieUiModel
import dev.odaridavid.smoovie.movies.components.MovieCard
import dev.odaridavid.smoovie.theme.EmptyContent
import dev.odaridavid.smoovie.theme.SmoovieTheme
import dev.odaridavid.smoovie.ui.SetStatusBarIcons
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import previewMovieUiModels
import smoovie.composeapp.generated.resources.Res
import smoovie.composeapp.generated.resources.watchlist_empty
import smoovie.composeapp.generated.resources.watchlist_title
import kotlin.math.roundToInt

@Composable
fun WatchlistScreen(
    viewModel: WatchlistViewModel,
    onMovieClick: (MovieUiModel) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    WatchlistContent(
        state = state,
        onMovieClick = onMovieClick,
        onRemove = viewModel::remove,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WatchlistContent(
    state: WatchlistUiState,
    onMovieClick: (MovieUiModel) -> Unit,
    onRemove: (MovieUiModel) -> Unit = {},
) {
    SetStatusBarIcons(useDarkIcons = !isSystemInDarkTheme())
    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(Res.string.watchlist_title)) },
            )
        },
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (state) {
                WatchlistUiState.Loading -> Unit

                WatchlistUiState.Empty -> {
                    EmptyContent(
                        imageVector = Icons.Default.Bookmark,
                        message = stringResource(Res.string.watchlist_empty),
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                    )
                }

                is WatchlistUiState.Loaded -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        itemsIndexed(items = state.movies, key = { _, movie -> movie.id }) { index, movie ->
                            val dismissState = rememberSwipeToDismissBoxState()
                            LaunchedEffect(dismissState.currentValue) {
                                if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
                                    onRemove(movie)
                                }
                            }
                            val peekOffset = remember { Animatable(0f) }
                            if (index == 0) {
                                LaunchedEffect(Unit) {
                                    delay(600)
                                    peekOffset.animateTo(-96f, tween(300))
                                    delay(80)
                                    peekOffset.animateTo(
                                        0f,
                                        spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                    )
                                }
                            }
                            SwipeToDismissBox(
                                state = dismissState,
                                backgroundContent = {
                                    Box(
                                        modifier =
                                            Modifier
                                                .fillMaxSize()
                                                .clip(MaterialTheme.shapes.medium)
                                                .background(MaterialTheme.colorScheme.errorContainer),
                                        contentAlignment =
                                            if (dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd) {
                                                Alignment.CenterStart
                                            } else {
                                                Alignment.CenterEnd
                                            },
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onErrorContainer,
                                            modifier = Modifier.padding(horizontal = 24.dp),
                                        )
                                    }
                                },
                            ) {
                                Box(
                                    modifier = Modifier.offset { IntOffset(peekOffset.value.roundToInt(), 0) },
                                ) {
                                    MovieCard(
                                        movie = movie,
                                        onClick = { onMovieClick(movie) },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun WatchlistEmptyPreview() {
    SmoovieTheme {
        WatchlistContent(state = WatchlistUiState.Empty, onMovieClick = {})
    }
}

@PreviewLightDark
@Composable
private fun WatchlistLoadedPreview() {
    SmoovieTheme {
        WatchlistContent(
            state = WatchlistUiState.Loaded(previewMovieUiModels),
            onMovieClick = {},
        )
    }
}
