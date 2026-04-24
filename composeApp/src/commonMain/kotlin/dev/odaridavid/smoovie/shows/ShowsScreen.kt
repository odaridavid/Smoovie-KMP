package dev.odaridavid.smoovie.shows

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import dev.odaridavid.smoovie.theme.SearchToolbar
import dev.odaridavid.smoovie.theme.ShimmerList
import dev.odaridavid.smoovie.shows.components.TvGenreChips
import dev.odaridavid.smoovie.shows.components.tvShowItems
import dev.odaridavid.smoovie.theme.EmptyContent
import dev.odaridavid.smoovie.theme.ErrorContent
import dev.odaridavid.smoovie.theme.SmoovieTheme
import dev.odaridavid.smoovie.ui.SearchBackHandler
import org.jetbrains.compose.resources.stringResource
import smoovie.composeapp.generated.resources.Res
import smoovie.composeapp.generated.resources.media_type_tv_shows
import smoovie.composeapp.generated.resources.search_shows_hint

private const val SLOW_ANIM_DURATION = 500

private data class ShowActions(
    val onSearchQueryChanged: (String) -> Unit = {},
    val onGenreSelected: (TvGenreUiModel?) -> Unit = {},
    val onRetry: () -> Unit = {},
    val onLoadMore: () -> Unit = {},
    val onTvShowClick: (TvShowUiModel) -> Unit = {},
)

@Composable
fun ShowsScreen(
    viewModel: ShowsViewModel,
    onTvShowClick: (TvShowUiModel) -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    ShowsContent(
        state = state,
        actions =
            ShowActions(
                onSearchQueryChanged = viewModel::onSearchQueryChanged,
                onGenreSelected = viewModel::onGenreSelected,
                onRetry = viewModel::retry,
                onLoadMore = viewModel::loadNextPage,
                onTvShowClick = onTvShowClick,
            ),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShowsContent(
    state: ShowsScreenState,
    actions: ShowActions,
) {
    var isSearchActive by rememberSaveable { mutableStateOf(false) }
    SearchBackHandler(enabled = isSearchActive) {
        isSearchActive = false
        actions.onSearchQueryChanged("")
    }
    val listState = rememberLazyListState()
    val animatedIds = remember { mutableSetOf<Int>() }

    val successState = state.uiState as? ShowsUiState.Success
    val firstShowId = successState?.tvShows?.firstOrNull()?.id
    LaunchedEffect(firstShowId) { animatedIds.clear() }

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
                label = "shows_topbar",
            ) { searchActive ->
                if (searchActive) {
                    SearchToolbar(
                        query = state.searchQuery,
                        onQueryChanged = actions.onSearchQueryChanged,
                        onClose = {
                            isSearchActive = false
                            actions.onSearchQueryChanged("")
                        },
                        placeholder = stringResource(Res.string.search_shows_hint),
                    )
                } else {
                    CenterAlignedTopAppBar(
                        title = { Text(text = stringResource(Res.string.media_type_tv_shows)) },
                        actions = {
                            IconButton(onClick = { isSearchActive = true }) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = stringResource(Res.string.search_shows_hint),
                                )
                            }
                        },
                    )
                }
            }
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (!isSearchActive && state.genres.isNotEmpty()) {
                TvGenreChips(
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
                modifier = Modifier.weight(1f),
            ) { uiState ->
                Box(modifier = Modifier.fillMaxSize()) {
                    when (uiState) {
                        is ShowsUiState.Loading -> {
                            ShimmerList(
                                modifier = Modifier.fillMaxSize(),
                                showHero = false,
                            )
                        }

                        is ShowsUiState.Empty -> {
                            EmptyContent(modifier = Modifier.align(Alignment.Center))
                        }

                        is ShowsUiState.Error -> {
                            ErrorContent(
                                error = uiState.error,
                                onRetry = actions.onRetry,
                                modifier = Modifier.align(Alignment.Center),
                            )
                        }

                        is ShowsUiState.Success -> {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                tvShowItems(
                                    tvShows = uiState.tvShows,
                                    animatedIds = animatedIds,
                                    isLoadingMore = uiState.isLoadingMore,
                                    onTvShowClick = actions.onTvShowClick,
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
        TvGenreUiModel(10759, "Action & Adventure"),
        TvGenreUiModel(35, "Comedy"),
        TvGenreUiModel(18, "Drama"),
    )

private val previewTvShows =
    listOf(
        TvShowUiModel(
            id = 1,
            name = "Breaking Bad",
            overview = "A high school chemistry teacher turned methamphetamine manufacturer.",
            firstAirDate = "20 Jan 2008",
            voteAverage = "9.5",
            backdropUrl = null,
            posterUrl = null,
        ),
        TvShowUiModel(
            id = 2,
            name = "Stranger Things",
            overview = "When a young boy disappears, his friends uncover a mystery.",
            firstAirDate = "15 Jul 2016",
            voteAverage = "8.7",
            backdropUrl = null,
            posterUrl = null,
        ),
    )

@PreviewLightDark
@Composable
private fun ShowsLoadingPreview() {
    SmoovieTheme {
        ShowsContent(
            state = ShowsScreenState(uiState = ShowsUiState.Loading, genres = previewGenres),
            actions = ShowActions(),
        )
    }
}

@PreviewLightDark
@Composable
private fun ShowsSuccessPreview() {
    SmoovieTheme {
        ShowsContent(
            state =
                ShowsScreenState(
                    uiState = ShowsUiState.Success(previewTvShows),
                    genres = previewGenres,
                    selectedGenre = previewGenres[1],
                ),
            actions = ShowActions(),
        )
    }
}

// endregion
