package dev.odaridavid.smoovie.movies

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import dev.odaridavid.smoovie.movies.components.CollapsedToolbar
import dev.odaridavid.smoovie.movies.components.MovieCard
import dev.odaridavid.smoovie.movies.components.SearchToolbar
import dev.odaridavid.smoovie.movies.components.ShimmerMovieList
import dev.odaridavid.smoovie.theme.EmptyContent
import dev.odaridavid.smoovie.theme.ErrorContent
import dev.odaridavid.smoovie.theme.SmoovieTheme
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import previewMovieUiModels
import smoovie.composeapp.generated.resources.Res
import smoovie.composeapp.generated.resources.genre_filter_all

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
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val genres by viewModel.genres.collectAsState()
    val selectedGenre by viewModel.selectedGenre.collectAsState()
    MoviesContent(
        uiState = uiState,
        searchQuery = searchQuery,
        genres = genres,
        selectedGenre = selectedGenre,
        actions = MovieActions(
            onSearchQueryChanged = viewModel::onSearchQueryChanged,
            onGenreSelected = viewModel::onGenreSelected,
            onRetry = viewModel::loadMovies,
            onLoadMore = viewModel::loadNextPage,
            onMovieClick = onMovieClick,
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoviesContent(
    uiState: MoviesUiState,
    searchQuery: String,
    genres: List<GenreUiModel>,
    selectedGenre: GenreUiModel?,
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
                        query = searchQuery,
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
            if (genres.isNotEmpty()) {
                GenreChips(
                    genres = genres,
                    selectedGenre = selectedGenre,
                    onGenreSelected = actions.onGenreSelected,
                )
            }
            AnimatedContent(
                targetState = uiState,
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

@Composable
private fun MoviesList(
    movies: List<MovieUiModel>,
    isLoadingMore: Boolean,
    hasMorePages: Boolean,
    onLoadMore: () -> Unit,
    onMovieClick: (MovieUiModel) -> Unit,
) {
    val listState = rememberLazyListState()
    val animatedIds = remember { mutableSetOf<Int>() }
    val firstMovieId = movies.firstOrNull()?.id
    LaunchedEffect(firstMovieId) { animatedIds.clear() }
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible =
                listState.layoutInfo.visibleItemsInfo
                    .lastOrNull()
                    ?.index ?: return@derivedStateOf false
            lastVisible >= listState.layoutInfo.totalItemsCount - 3
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && hasMorePages && !isLoadingMore) onLoadMore()
    }
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        itemsIndexed(movies, key = { _, movie -> movie.id }) { index, movie ->
            AnimatedMovieCard(
                movie = movie,
                index = index,
                skipAnimation = movie.id in animatedIds,
                onAnimationEnd = { animatedIds.add(movie.id) },
                onClick = { onMovieClick(movie) },
            )
        }
        if (isLoadingMore) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun GenreChips(
    genres: List<GenreUiModel>,
    selectedGenre: GenreUiModel?,
    onGenreSelected: (GenreUiModel?) -> Unit,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            FilterChip(
                selected = selectedGenre == null,
                onClick = { onGenreSelected(null) },
                label = { Text(stringResource(Res.string.genre_filter_all)) },
            )
        }
        items(genres, key = { it.id }) { genre ->
            FilterChip(
                selected = selectedGenre?.id == genre.id,
                onClick = { onGenreSelected(if (selectedGenre?.id == genre.id) null else genre) },
                label = { Text(genre.name) },
            )
        }
    }
}

private const val CARD_ANIM_DURATION = 350
private const val CARD_STAGGER_MS = 60
private const val CARD_STAGGER_LIMIT = 5

@Composable
private fun AnimatedMovieCard(
    movie: MovieUiModel,
    index: Int,
    skipAnimation: Boolean,
    onAnimationEnd: () -> Unit,
    onClick: () -> Unit,
) {
    if (index >= CARD_STAGGER_LIMIT || skipAnimation) {
        MovieCard(movie = movie, onClick = onClick)
        return
    }
    var entered by remember { mutableStateOf(false) }
    val staggerDelay = index * CARD_STAGGER_MS
    LaunchedEffect(Unit) {
        entered = true
        delay((staggerDelay + CARD_ANIM_DURATION).toLong())
        onAnimationEnd()
    }
    val spec = tween<Float>(CARD_ANIM_DURATION, delayMillis = staggerDelay)
    val scaleX by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = spec,
        label = "scaleX",
    )
    val alpha by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = spec,
        label = "alpha",
    )
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    this.scaleX = scaleX
                    this.alpha = alpha
                },
    ) {
        MovieCard(movie = movie, onClick = onClick)
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
            uiState = MoviesUiState.Loading,
            searchQuery = "",
            genres = previewGenres,
            selectedGenre = null,
            actions = MovieActions(),
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
            genres = previewGenres,
            selectedGenre = previewGenres[0],
            actions = MovieActions(),
        )
    }
}

// endregion
