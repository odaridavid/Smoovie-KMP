package dev.odaridavid.smoovie.watchlist

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import dev.odaridavid.smoovie.shows.TvShowUiModel
import dev.odaridavid.smoovie.shows.components.TvShowCard
import dev.odaridavid.smoovie.theme.EmptyContent
import dev.odaridavid.smoovie.theme.SmoovieTheme
import dev.odaridavid.smoovie.ui.SetStatusBarIcons
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import dev.odaridavid.smoovie.utils.previewMovieUiModels
import smoovie.composeapp.generated.resources.Res
import smoovie.composeapp.generated.resources.media_type_movies
import smoovie.composeapp.generated.resources.media_type_tv_shows
import smoovie.composeapp.generated.resources.watchlist_empty
import smoovie.composeapp.generated.resources.watchlist_empty_filter
import smoovie.composeapp.generated.resources.watchlist_filter_all
import smoovie.composeapp.generated.resources.watchlist_title
import kotlin.math.roundToInt

@Composable
fun WatchlistScreen(
    viewModel: WatchlistViewModel,
    onMovieClick: (MovieUiModel) -> Unit,
    onTvShowClick: (TvShowUiModel) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    WatchlistContent(
        state = state,
        onMovieClick = onMovieClick,
        onTvShowClick = onTvShowClick,
        onFilterChange = viewModel::setFilter,
        onRemove = viewModel::remove,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WatchlistContent(
    state: WatchlistUiState,
    onMovieClick: (MovieUiModel) -> Unit,
    onTvShowClick: (TvShowUiModel) -> Unit = {},
    onFilterChange: (WatchlistFilter) -> Unit = {},
    onRemove: (WatchlistItemUiModel) -> Unit = {},
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
                    LoadedColumn(
                        state = state,
                        onMovieClick = onMovieClick,
                        onTvShowClick = onTvShowClick,
                        onFilterChange = onFilterChange,
                        onRemove = onRemove,
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadedColumn(
    state: WatchlistUiState.Loaded,
    onMovieClick: (MovieUiModel) -> Unit,
    onTvShowClick: (TvShowUiModel) -> Unit,
    onFilterChange: (WatchlistFilter) -> Unit,
    onRemove: (WatchlistItemUiModel) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        FilterChips(
            selected = state.filter,
            onFilterChange = onFilterChange,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        )
        if (state.items.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize()) {
                EmptyContent(
                    imageVector = Icons.Default.Bookmark,
                    message = stringResource(Res.string.watchlist_empty_filter),
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                itemsIndexed(items = state.items, key = { _, item -> item.key }) { index, item ->
                    WatchlistRow(
                        item = item,
                        showSwipeHint = index == 0,
                        onMovieClick = onMovieClick,
                        onTvShowClick = onTvShowClick,
                        onRemove = onRemove,
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterChips(
    selected: WatchlistFilter,
    onFilterChange: (WatchlistFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        WatchlistFilter.entries.forEach { filter ->
            FilterChip(
                selected = filter == selected,
                onClick = { onFilterChange(filter) },
                label = { Text(stringResource(filter.labelRes())) },
            )
        }
    }
}

private fun WatchlistFilter.labelRes(): StringResource =
    when (this) {
        WatchlistFilter.ALL -> Res.string.watchlist_filter_all
        WatchlistFilter.MOVIES -> Res.string.media_type_movies
        WatchlistFilter.TV_SHOWS -> Res.string.media_type_tv_shows
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WatchlistRow(
    item: WatchlistItemUiModel,
    showSwipeHint: Boolean,
    onMovieClick: (MovieUiModel) -> Unit,
    onTvShowClick: (TvShowUiModel) -> Unit,
    onRemove: (WatchlistItemUiModel) -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState()
    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
            onRemove(item)
        }
    }
    val peekOffset = remember { Animatable(0f) }
    if (showSwipeHint) {
        LaunchedEffect(Unit) {
            delay(600)
            peekOffset.animateTo(-96f, tween(300))
            delay(80)
            peekOffset.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
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
        Box(modifier = Modifier.offset { IntOffset(peekOffset.value.roundToInt(), 0) }) {
            when (item) {
                is WatchlistItemUiModel.Movie ->
                    MovieCard(movie = item.movie, onClick = { onMovieClick(item.movie) })

                is WatchlistItemUiModel.TvShow ->
                    TvShowCard(tvShow = item.tvShow, onClick = { onTvShowClick(item.tvShow) })
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
            state =
                WatchlistUiState.Loaded(
                    filter = WatchlistFilter.ALL,
                    items = previewMovieUiModels.map { WatchlistItemUiModel.Movie(it) },
                ),
            onMovieClick = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun WatchlistLoadedEmptyFilterPreview() {
    SmoovieTheme {
        WatchlistContent(
            state =
                WatchlistUiState.Loaded(
                    filter = WatchlistFilter.TV_SHOWS,
                    items = emptyList(),
                ),
            onMovieClick = {},
        )
    }
}
