package dev.odaridavid.smoovie.movies

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import dev.odaridavid.smoovie.movies.components.CollapsedToolbar
import dev.odaridavid.smoovie.ui.SearchBackHandler
import dev.odaridavid.smoovie.movies.components.FeaturedMoviesPager
import dev.odaridavid.smoovie.movies.components.GenreChips
import dev.odaridavid.smoovie.movies.components.SearchToolbar
import dev.odaridavid.smoovie.movies.components.ShimmerMovieList
import dev.odaridavid.smoovie.movies.components.movieItems
import dev.odaridavid.smoovie.theme.EmptyContent
import dev.odaridavid.smoovie.theme.ErrorContent
import dev.odaridavid.smoovie.theme.SmoovieTheme
import previewMovieUiModels

private const val SLOW_ANIM_DURATION = 500
private const val FEATURED_COUNT = 4

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
    SearchBackHandler(enabled = isSearchActive) {
        isSearchActive = false
        actions.onSearchQueryChanged("")
    }
    val listState = rememberLazyListState()
    val animatedIds = remember { mutableSetOf<Int>() }

    val successState = state.uiState as? MoviesUiState.Success
    val firstMovieId = successState?.movies?.firstOrNull()?.id
    LaunchedEffect(firstMovieId) { animatedIds.clear() }

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible =
                listState.layoutInfo.visibleItemsInfo
                    .lastOrNull()
                    ?.index
                    ?: return@derivedStateOf false
            lastVisible >= listState.layoutInfo.totalItemsCount - 3
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && successState?.hasMorePages == true && successState.isLoadingMore.not()) {
            actions.onLoadMore()
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            AnimatedContent(
                targetState = isSearchActive,
                transitionSpec = {
                    (fadeIn(tween(350)) togetherWith fadeOut(tween(300)))
                        .using(SizeTransform(clip = true))
                },
                label = "topbar",
            ) { searchActive ->
                if (searchActive) {
                    SearchToolbar(
                        query = state.searchQuery,
                        onQueryChanged = actions.onSearchQueryChanged,
                        onClose = {
                            isSearchActive = false
                            actions.onSearchQueryChanged("")
                        },
                    )
                } else if (state.featuredMovies.isEmpty() && state.uiState !is MoviesUiState.Loading) {
                    CollapsedToolbar(
                        visible = true,
                        onIconClick = { isSearchActive = true },
                    )
                }
            }
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            AnimatedVisibility(
                visible = !isSearchActive && state.featuredMovies.isNotEmpty(),
                enter = expandVertically(tween(400)) + fadeIn(tween(400)),
                exit = shrinkVertically(tween(350)) + fadeOut(tween(300)),
            ) {
                Column {
                    FeaturedMoviesPager(
                        movies = state.featuredMovies.take(FEATURED_COUNT),
                        onSearchClick = { isSearchActive = true },
                        onMovieClick = actions.onMovieClick,
                    )
                    if (state.genres.isNotEmpty()) {
                        GenreChips(
                            genres = state.genres,
                            selectedGenre = state.selectedGenre,
                            onGenreSelected = actions.onGenreSelected,
                        )
                    }
                }
            }
            AnimatedContent(
                targetState = state.uiState,
                transitionSpec = {
                    fadeIn(tween(SLOW_ANIM_DURATION)) togetherWith fadeOut(tween(SLOW_ANIM_DURATION))
                },
                contentKey = { it::class },
                modifier = Modifier.weight(1f),
            ) { uiState ->
                Box(modifier = Modifier.fillMaxSize()) {
                    when (uiState) {
                        is MoviesUiState.Loading -> {
                            ShimmerMovieList(
                                modifier = Modifier.fillMaxSize(),
                                showHero = state.featuredMovies.isEmpty(),
                                onSearchClick = { isSearchActive = true },
                            )
                        }

                        is MoviesUiState.Empty -> {
                            EmptyContent(modifier = Modifier.align(Alignment.Center))
                        }

                        is MoviesUiState.Error -> {
                            ErrorContent(
                                message = uiState.message,
                                onRetry = actions.onRetry,
                                modifier = Modifier.align(Alignment.Center),
                            )
                        }

                        is MoviesUiState.Success -> {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                movieItems(
                                    movies = uiState.movies,
                                    animatedIds = animatedIds,
                                    isLoadingMore = uiState.isLoadingMore,
                                    onMovieClick = actions.onMovieClick,
                                )
                            }
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
                    featuredMovies = previewMovieUiModels,
                ),
            actions = MovieActions(),
        )
    }
}

// endregion
