package dev.odaridavid.smoovie.shows

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import dev.odaridavid.smoovie.shows.components.EpisodeItem
import dev.odaridavid.smoovie.theme.ErrorContent
import dev.odaridavid.smoovie.theme.ExpandableText
import dev.odaridavid.smoovie.theme.SmoovieTheme
import org.jetbrains.compose.resources.stringResource
import smoovie.composeapp.generated.resources.Res
import smoovie.composeapp.generated.resources.error_season_detail_failed
import smoovie.composeapp.generated.resources.navigate_back

@Composable
fun SeasonDetailScreen(
    viewModel: SeasonDetailViewModel,
    seasonName: String,
    onBack: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    SeasonDetailContent(
        seasonName = seasonName,
        state = state,
        onBack = onBack,
        onRetry = viewModel::loadSeasonDetail,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SeasonDetailContent(
    seasonName: String,
    state: SeasonDetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = (state as? SeasonDetailUiState.Success)?.seasonDetail?.name ?: seasonName) },
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
                is SeasonDetailUiState.Loading -> Unit

                is SeasonDetailUiState.Success -> {
                    val detail = state.seasonDetail
                    val bottomInset =
                        WindowInsets.navigationBars
                            .asPaddingValues()
                            .calculateBottomPadding()
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding =
                            PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                top = 16.dp,
                                bottom = 16.dp + bottomInset,
                            ),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        item {
                            SeasonHeader(detail)
                        }
                        items(detail.episodes, key = { it.id }) { episode ->
                            EpisodeItem(episode = episode)
                        }
                    }
                }

                is SeasonDetailUiState.Error -> {
                    ErrorContent(
                        error = state.error,
                        title = stringResource(Res.string.error_season_detail_failed),
                        onRetry = onRetry,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            }
        }
    }
}

@Composable
private fun SeasonHeader(detail: SeasonDetailUiModel) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val subtitle =
            listOf(detail.year, detail.episodeCountLabel)
                .filter { it.isNotBlank() }
                .joinToString(" · ")
        if (subtitle.isNotBlank()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (detail.overview.isNotBlank()) {
            ExpandableText(
                text = detail.overview,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

// region Previews

private val previewSeasonDetail =
    SeasonDetailUiModel(
        id = 1,
        seasonNumber = 1,
        name = "Season 1",
        overview = "Walter White's diagnosis leads to a fateful decision.",
        year = "2008",
        posterUrl = null,
        episodeCountLabel = "7 episodes",
        episodes =
            listOf(
                EpisodeUiModel(
                    id = 1,
                    episodeNumber = 1,
                    name = "Pilot",
                    overview = "Walter White's transformation begins.",
                    airDate = "20 Jan 2008",
                    runtimeLabel = "58 min",
                    voteAverage = "8.3",
                    stillUrl = null,
                    headerLabel = "Ep 1 · Pilot",
                ),
                EpisodeUiModel(
                    id = 2,
                    episodeNumber = 2,
                    name = "Cat's in the Bag...",
                    overview = "Walt and Jesse deal with the aftermath.",
                    airDate = "27 Jan 2008",
                    runtimeLabel = "48 min",
                    voteAverage = "8.2",
                    stillUrl = null,
                    headerLabel = "Ep 2 · Cat's in the Bag...",
                ),
            ),
    )

@PreviewLightDark
@Composable
private fun SeasonDetailSuccessPreview() {
    SmoovieTheme {
        SeasonDetailContent(
            seasonName = "Season 1",
            state = SeasonDetailUiState.Success(previewSeasonDetail),
            onBack = {},
            onRetry = {},
        )
    }
}

// endregion
