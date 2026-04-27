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
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import dev.odaridavid.smoovie.filter.FilterGenreOption
import dev.odaridavid.smoovie.filter.MovieSortOption
import dev.odaridavid.smoovie.filter.SortEntry
import dev.odaridavid.smoovie.movies.components.CollapsedToolbar
import dev.odaridavid.smoovie.movies.components.FeaturedMoviesPager
import dev.odaridavid.smoovie.movies.components.movieItems
import dev.odaridavid.smoovie.theme.EmptyContent
import dev.odaridavid.smoovie.theme.ErrorContent
import dev.odaridavid.smoovie.theme.SearchToolbar
import dev.odaridavid.smoovie.theme.ShimmerHero
import dev.odaridavid.smoovie.theme.ShimmerList
import dev.odaridavid.smoovie.theme.SmoovieTheme
import dev.odaridavid.smoovie.ui.FilterSheet
import dev.odaridavid.smoovie.ui.SearchBackHandler
import dev.odaridavid.smoovie.ui.SetStatusBarIcons
import dev.odaridavid.smoovie.utils.AppError
import dev.odaridavid.smoovie.utils.previewMovieUiModels

private const val SLOW_ANIM_DURATION = 500
private const val FEATURED_COUNT = 4
private val SHEET_OVERLAP = 28.dp
private val SheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)

private val movieSortEntries = MovieSortOption.entries.map { SortEntry(it.label, it.apiValue) }

private data class MovieActions(
    val onSearchQueryChanged: (String) -> Unit = {},
    val onFilterApplied: (Int?, String, Float) -> Unit = { _, _, _ -> },
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
                onFilterApplied = viewModel::onFilterApplied,
                onRetry = viewModel::retry,
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
    var isFilterSheetVisible by remember { mutableStateOf(false) }
    SearchBackHandler(enabled = isSearchActive) {
        isSearchActive = false
        actions.onSearchQueryChanged("")
    }

    val heroVisible =
        !isSearchActive &&
            state.uiState !is MoviesUiState.Error &&
            (state.featuredMovies.isNotEmpty() || state.uiState is MoviesUiState.Loading)
    SetStatusBarIcons(useDarkIcons = !isSystemInDarkTheme() && !heroVisible)

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

    val background = MaterialTheme.colorScheme.background

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
                } else if (state.uiState is MoviesUiState.Error ||
                    (state.featuredMovies.isEmpty() && state.uiState !is MoviesUiState.Loading)
                ) {
                    CollapsedToolbar(
                        visible = true,
                        isFilterActive = state.filterPreferences.isActive,
                        onSearchClick = { isSearchActive = true },
                        onFilterClick = { isFilterSheetVisible = true },
                    )
                }
            }
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            AnimatedVisibility(
                visible = heroVisible,
                enter = expandVertically(tween(400)) + fadeIn(tween(400)),
                exit = shrinkVertically(tween(350)) + fadeOut(tween(300)),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .layout { measurable, constraints ->
                            val placeable = measurable.measure(constraints)
                            val overlapPx = SHEET_OVERLAP.roundToPx()
                            val reportedHeight = (placeable.height - overlapPx).coerceAtLeast(0)
                            layout(placeable.width, reportedHeight) {
                                placeable.place(0, 0)
                            }
                        },
            ) {
                AnimatedContent(
                    targetState = state.featuredMovies.isNotEmpty(),
                    transitionSpec = {
                        fadeIn(tween(SLOW_ANIM_DURATION)) togetherWith fadeOut(tween(SLOW_ANIM_DURATION))
                    },
                    label = "featured",
                ) { hasFeatured ->
                    if (hasFeatured) {
                        FeaturedMoviesPager(
                            movies = state.featuredMovies.take(FEATURED_COUNT),
                            onSearchClick = { isSearchActive = true },
                            onFilterClick = { isFilterSheetVisible = true },
                            isFilterActive = state.filterPreferences.isActive,
                            onMovieClick = actions.onMovieClick,
                        )
                    } else {
                        ShimmerHero(
                            onSearchClick = { isSearchActive = true },
                            onFilterClick = { isFilterSheetVisible = true },
                            isFilterActive = state.filterPreferences.isActive,
                        )
                    }
                }
            }
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(background, SheetShape)
                        .padding(top = 12.dp),
            ) {
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
                                ShimmerList(modifier = Modifier.fillMaxSize())
                            }

                            is MoviesUiState.Empty -> {
                                EmptyContent(modifier = Modifier.align(Alignment.Center))
                            }

                            is MoviesUiState.Error -> {
                                ErrorContent(
                                    error = uiState.error,
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

    if (isFilterSheetVisible) {
        val filter = state.filterPreferences
        FilterSheet(
            genres = state.genres.map { FilterGenreOption(it.id, it.name) },
            sortEntries = movieSortEntries,
            selectedGenreId = filter.selectedGenreId,
            selectedSortApiValue = filter.sortBy.apiValue,
            minRating = filter.minRating,
            onApply = { genreId, sortEntry, rating ->
                actions.onFilterApplied(genreId, sortEntry.apiValue, rating)
                isFilterSheetVisible = false
            },
            onDismiss = { isFilterSheetVisible = false },
        )
    }
}

// region Previews

@PreviewLightDark
@Composable
private fun MoviesLoadingPreview() {
    SmoovieTheme {
        MoviesContent(
            state = MoviesScreenState(uiState = MoviesUiState.Loading),
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
                    featuredMovies = previewMovieUiModels,
                ),
            actions = MovieActions(),
        )
    }
}

@PreviewLightDark
@Composable
private fun MoviesErrorPreview() {
    SmoovieTheme {
        MoviesContent(
            state = MoviesScreenState(uiState = MoviesUiState.Error(AppError.NetworkError)),
            actions = MovieActions(),
        )
    }
}

// endregion
