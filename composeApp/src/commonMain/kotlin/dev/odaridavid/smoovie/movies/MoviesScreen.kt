package dev.odaridavid.smoovie.movies

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import dev.odaridavid.smoovie.theme.EmptyContent
import dev.odaridavid.smoovie.theme.ErrorContent
import dev.odaridavid.smoovie.theme.SmoovieTheme
import previewMovieUiModels

private const val SLOW_ANIM_DURATION = 500

@Composable
fun MoviesScreen(viewModel: MoviesViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    MoviesContent(
        uiState = uiState,
        searchQuery = searchQuery,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onRetry = viewModel::loadMovies,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoviesContent(
    uiState: MoviesUiState,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onRetry: () -> Unit,
) {
    var isSearchActive by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Box {
                CollapsedToolbar(
                    visible = !isSearchActive,
                    onIconClick = { isSearchActive = true },
                )
                AnimatedVisibility(
                    visible = isSearchActive,
                    enter = fadeIn(tween(SLOW_ANIM_DURATION)),
                    exit = fadeOut(tween(SLOW_ANIM_DURATION)),
                ) {
                    SearchToolbar(
                        query = searchQuery,
                        onQueryChanged = onSearchQueryChanged,
                        onClose = {
                            isSearchActive = false
                            onSearchQueryChanged("")
                        },
                    )
                }
            }
        },
    ) { padding ->
        AnimatedContent(
            targetState = uiState,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            contentKey = { it::class },
            modifier =
                Modifier
                    .padding(padding)
                    .fillMaxSize(),
        ) { state ->
            Box(modifier = Modifier.fillMaxSize()) {
                when (state) {
                    is MoviesUiState.Loading -> {
                        ShimmerMovieList(modifier = Modifier.fillMaxSize())
                    }

                    is MoviesUiState.Success -> {
                        MoviesList(movies = state.movies)
                    }

                    is MoviesUiState.Empty -> {
                        EmptyContent(modifier = Modifier.align(Alignment.Center))
                    }

                    is MoviesUiState.Error -> {
                        ErrorContent(
                            message = state.message,
                            onRetry = onRetry,
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MoviesList(movies: List<MovieUiModel>) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(movies, key = { it.id }) { movie ->
            MovieCard(movie = movie)
        }
    }
}

// region Previews

@PreviewLightDark
@Composable
private fun MoviesLoadingPreview() {
    SmoovieTheme {
        MoviesContent(
            uiState = MoviesUiState.Loading,
            searchQuery = "",
            onSearchQueryChanged = {},
            onRetry = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun MoviesSuccessPreview() {
    SmoovieTheme {
        MoviesContent(
            uiState = MoviesUiState.Success(previewMovieUiModels),
            searchQuery = "",
            onSearchQueryChanged = {},
            onRetry = {},
        )
    }
}

// endregion
