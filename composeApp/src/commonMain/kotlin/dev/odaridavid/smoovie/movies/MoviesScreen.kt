package dev.odaridavid.smoovie.movies

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import dev.odaridavid.smoovie.movies.components.CollapsedToolbar
import dev.odaridavid.smoovie.movies.components.GenreChips
import dev.odaridavid.smoovie.movies.components.MoviesList
import dev.odaridavid.smoovie.movies.components.SearchToolbar
import dev.odaridavid.smoovie.movies.components.ShimmerMovieList
import dev.odaridavid.smoovie.theme.EmptyContent
import dev.odaridavid.smoovie.theme.ErrorContent
import dev.odaridavid.smoovie.theme.SmoovieTheme
import previewMovieUiModels

private const val SLOW_ANIM_DURATION = 500

private data class MovieActions(
    val onSearchQueryChanged: (String) -> Unit = {},
    val onGenreSelected: (GenreUiModel?) -> Unit = {},
    val onRetry: () -> Unit = {},
    val onLoadMore: () -> Unit = {},
    val onMovieClick: (MovieUiModel) -> Unit = {},
)

@Composable
fun MoviesScreen(
    viewModel: MoviesViewModel,
    onMovieClick: (MovieUiModel) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    MoviesContent(
        state = state,
        actions =
            MovieActions(
                onSearchQueryChanged = viewModel::onSearchQueryChanged,
                onGenreSelected = viewModel::onGenreSelected,
                onRetry = viewModel::loadMovies,
                onLoadMore = viewModel::loadNextPage,
                onMovieClick = onMovieClick,
            ),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoviesContent(
    state: MoviesScreenState,
    actions: MovieActions,
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
                        query = state.searchQuery,
                        onQueryChanged = actions.onSearchQueryChanged,
                        onClose = {
                            isSearchActive = false
                            actions.onSearchQueryChanged("")
                        },
                    )
                }
            }
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .padding(padding)
                    .fillMaxSize(),
        ) {
            if (state.genres.isNotEmpty() && state.searchQuery.isBlank()) {
                GenreChips(
                    genres = state.genres,
                    selectedGenre = state.selectedGenre,
                    onGenreSelected = actions.onGenreSelected,
                )
            }
            AnimatedContent(
                targetState = state.uiState,
                transitionSpec = {
                    fadeIn(tween(SLOW_ANIM_DURATION)) togetherWith fadeOut(tween(SLOW_ANIM_DURATION))
                },
                contentKey = { it::class },
                modifier = Modifier.fillMaxSize(),
            ) { state ->
                Box(modifier = Modifier.fillMaxSize()) {
                    when (state) {
                        is MoviesUiState.Loading -> {
                            ShimmerMovieList(modifier = Modifier.fillMaxSize())
                        }

                        is MoviesUiState.Success -> {
                            MoviesList(
                                movies = state.movies,
                                isLoadingMore = state.isLoadingMore,
                                hasMorePages = state.hasMorePages,
                                onLoadMore = actions.onLoadMore,
                                onMovieClick = actions.onMovieClick,
                            )
                        }

                        is MoviesUiState.Empty -> {
                            EmptyContent(modifier = Modifier.align(Alignment.Center))
                        }

                        is MoviesUiState.Error -> {
                            ErrorContent(
                                message = state.message,
                                onRetry = actions.onRetry,
                                modifier = Modifier.align(Alignment.Center),
                            )
                        }
                    }
                }
            }
        }
    }
}

// region Previews

private val previewGenres =
    listOf(
        GenreUiModel(28, "Action"),
        GenreUiModel(35, "Comedy"),
        GenreUiModel(18, "Drama"),
    )

@PreviewLightDark
@Composable
private fun MoviesLoadingPreview() {
    SmoovieTheme {
        MoviesContent(
            state = MoviesScreenState(uiState = MoviesUiState.Loading, genres = previewGenres),
            actions = MovieActions(),
        )
    }
}

@PreviewLightDark
@Composable
private fun MoviesSuccessPreview() {
    SmoovieTheme {
        MoviesContent(
            state =
                MoviesScreenState(
                    uiState = MoviesUiState.Success(previewMovieUiModels),
                    genres = previewGenres,
                    selectedGenre = previewGenres[0],
                ),
            actions = MovieActions(),
        )
    }
}

// endregion
