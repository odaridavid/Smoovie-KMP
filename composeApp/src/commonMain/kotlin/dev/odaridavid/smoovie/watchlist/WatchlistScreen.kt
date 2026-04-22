package dev.odaridavid.smoovie.watchlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import dev.odaridavid.smoovie.movies.MovieUiModel
import dev.odaridavid.smoovie.movies.components.MovieCard
import dev.odaridavid.smoovie.theme.EmptyContent
import dev.odaridavid.smoovie.theme.SmoovieTheme
import org.jetbrains.compose.resources.stringResource
import previewMovieUiModels
import smoovie.composeapp.generated.resources.Res
import smoovie.composeapp.generated.resources.navigate_back
import smoovie.composeapp.generated.resources.watchlist_empty
import smoovie.composeapp.generated.resources.watchlist_title

@Composable
fun WatchlistScreen(
    viewModel: WatchlistViewModel,
    onBack: () -> Unit,
    onMovieClick: (MovieUiModel) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    WatchlistContent(
        state = state,
        onBack = onBack,
        onMovieClick = onMovieClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WatchlistContent(
    state: WatchlistUiState,
    onBack: () -> Unit,
    onMovieClick: (MovieUiModel) -> Unit,
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(Res.string.watchlist_title)) },
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
                        items(items = state.movies, key = { it.id }) { movie ->
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

@PreviewLightDark
@Composable
private fun WatchlistEmptyPreview() {
    SmoovieTheme {
        WatchlistContent(state = WatchlistUiState.Empty, onBack = {}, onMovieClick = {})
    }
}

@PreviewLightDark
@Composable
private fun WatchlistLoadedPreview() {
    SmoovieTheme {
        WatchlistContent(
            state = WatchlistUiState.Loaded(previewMovieUiModels),
            onBack = {},
            onMovieClick = {},
        )
    }
}
