package dev.odaridavid.smoovie.shows

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import dev.odaridavid.smoovie.movies.components.CastSection
import dev.odaridavid.smoovie.movies.components.ReviewsSection
import dev.odaridavid.smoovie.movies.components.TrailersSection
import dev.odaridavid.smoovie.movies.components.WhereToWatchSection
import dev.odaridavid.smoovie.person.PersonSummaryUiModel
import dev.odaridavid.smoovie.shows.components.SeasonsSection
import dev.odaridavid.smoovie.shows.components.SimilarTvShowsSection
import dev.odaridavid.smoovie.theme.ExpandableText
import dev.odaridavid.smoovie.theme.HeroSection
import dev.odaridavid.smoovie.theme.KeywordChips
import dev.odaridavid.smoovie.theme.MetadataRow
import dev.odaridavid.smoovie.theme.ShimmerDetail
import dev.odaridavid.smoovie.theme.SmoovieTheme
import dev.odaridavid.smoovie.ui.SetStatusBarIcons
import dev.odaridavid.smoovie.utils.AppError
import org.jetbrains.compose.resources.stringResource
import smoovie.composeapp.generated.resources.Res
import smoovie.composeapp.generated.resources.action_retry
import smoovie.composeapp.generated.resources.error_network
import smoovie.composeapp.generated.resources.error_not_found
import smoovie.composeapp.generated.resources.error_server
import smoovie.composeapp.generated.resources.error_unauthorized
import smoovie.composeapp.generated.resources.error_unknown

@Composable
fun TvShowDetailScreen(
    viewModel: TvShowDetailViewModel,
    tvShow: TvShowUiModel,
    onBack: () -> Unit,
    onTvShowClick: (TvShowUiModel) -> Unit,
    onPersonClick: (PersonSummaryUiModel) -> Unit,
    onSeasonClick: (SeasonUiModel) -> Unit = {},
) {
    val detailState by viewModel.uiState.collectAsState()
    val isInWatchlist by viewModel.isInWatchlist.collectAsState()
    TvShowDetailContent(
        tvShow = tvShow,
        detailState = detailState,
        isInWatchlist = isInWatchlist,
        onBack = onBack,
        onRetry = viewModel::loadTvShowDetail,
        onToggleWatchlist = { viewModel.toggleWatchlist(tvShow) },
        onTvShowClick = onTvShowClick,
        onPersonClick = onPersonClick,
        onSeasonClick = onSeasonClick,
    )
}

@Composable
internal fun TvShowDetailContent(
    tvShow: TvShowUiModel,
    detailState: TvShowDetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    isInWatchlist: Boolean = false,
    onToggleWatchlist: () -> Unit = {},
    onTvShowClick: (TvShowUiModel) -> Unit = {},
    onPersonClick: (PersonSummaryUiModel) -> Unit = {},
    onSeasonClick: (SeasonUiModel) -> Unit = {},
) {
    SetStatusBarIcons(useDarkIcons = false)

    val background = MaterialTheme.colorScheme.background
    val sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    val sheetOverlap = 28.dp

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(background)
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.navigationBars),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints)
                        val overlapPx = sheetOverlap.roundToPx()
                        val reportedHeight = (placeable.height - overlapPx).coerceAtLeast(0)
                        layout(placeable.width, reportedHeight) {
                            placeable.place(0, 0)
                        }
                    },
        ) {
            HeroSection(
                backdropUrl = tvShow.backdropUrl,
                posterUrl = tvShow.posterUrl,
                onBack = onBack,
                isInWatchlist = isInWatchlist,
                onToggleWatchlist = onToggleWatchlist,
            )
        }
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(background, sheetShape)
                    .padding(top = 20.dp),
        ) {
            TitleHeader(
                title = tvShow.name,
                tagline = (detailState as? TvShowDetailUiState.Success)?.tvShowDetail?.tagline,
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            when (detailState) {
                is TvShowDetailUiState.Loading -> {
                    ShimmerDetail(modifier = Modifier.fillMaxWidth())
                }

                is TvShowDetailUiState.Success -> {
                    val detail = detailState.tvShowDetail
                    DetailBody(tvShow = tvShow, detail = detail)
                    if (detail.cast.isNotEmpty()) {
                        CastSection(
                            cast = detail.cast,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            onPersonClick = onPersonClick,
                        )
                    }
                    if (detail.streamingProviders.isNotEmpty() || detail.rentBuyProviders.isNotEmpty()) {
                        WhereToWatchSection(
                            streamingProviders = detail.streamingProviders,
                            rentBuyProviders = detail.rentBuyProviders,
                            link = detail.watchProvidersLink,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                        )
                    }
                    if (detail.seasons.isNotEmpty()) {
                        SeasonsSection(
                            seasons = detail.seasons,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                            onSeasonClick = onSeasonClick,
                        )
                    }
                    if (detail.trailers.isNotEmpty()) {
                        TrailersSection(
                            trailers = detail.trailers,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                        )
                    }
                    if (detail.reviews.isNotEmpty()) {
                        ReviewsSection(
                            reviews = detail.reviews,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                        )
                    }
                    if (detail.similar.isNotEmpty()) {
                        SimilarTvShowsSection(
                            tvShows = detail.similar,
                            onTvShowClick = onTvShowClick,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                        )
                    }
                }

                is TvShowDetailUiState.Error -> {
                    DetailBody(tvShow = tvShow) {
                        ErrorBanner(error = detailState.error, onRetry = onRetry)
                    }
                }
            }
        }
    }
}

@Composable
private fun TitleHeader(
    title: String,
    tagline: String?,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
        )
        if (!tagline.isNullOrBlank()) {
            Text(
                text = tagline,
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun DetailBody(
    tvShow: TvShowUiModel,
    detail: TvShowDetailUiModel? = null,
    extraContent: @Composable (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        MetadataSection(tvShow = tvShow, detail = detail)

        if (!detail?.keywords.isNullOrEmpty()) {
            KeywordChips(keywords = detail.keywords)
        }

        extraContent?.invoke()

        val overview = detail?.overview ?: tvShow.overview
        if (overview.isNotBlank()) {
            ExpandableText(
                text = overview,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun MetadataSection(
    tvShow: TvShowUiModel,
    detail: TvShowDetailUiModel? = null,
) {
    val rating =
        if (tvShow.voteAverage.isNotBlank()) {
            val voteCount = detail?.voteCount
            if (!voteCount.isNullOrBlank()) "★ ${tvShow.voteAverage} ($voteCount)" else "★ ${tvShow.voteAverage}"
        } else {
            null
        }
    val years = detail?.yearsRange?.takeIf { it.isNotBlank() } ?: tvShow.firstAirDate.takeIf { it.isNotBlank() }
    MetadataRow(
        rating,
        years,
        detail?.seasonsLabel,
        detail?.genres,
        detail?.ageRating,
        detail?.networks?.takeIf { it.isNotBlank() }?.let { "📺 $it" },
    )
}

@Composable
private fun ErrorBanner(
    error: AppError,
    onRetry: () -> Unit,
) {
    val message =
        stringResource(
            when (error) {
                AppError.NetworkError -> Res.string.error_network
                AppError.ServerError -> Res.string.error_server
                AppError.NotFound -> Res.string.error_not_found
                AppError.Unauthorized -> Res.string.error_unauthorized
                AppError.Unknown -> Res.string.error_unknown
            },
        )
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.errorContainer,
                    MaterialTheme.shapes.small,
                ).padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.align(Alignment.CenterStart),
        )
        TextButton(
            onClick = onRetry,
            modifier = Modifier.align(Alignment.CenterEnd),
        ) {
            Text(stringResource(Res.string.action_retry))
        }
    }
}

// region Previews

private val previewTvShow =
    TvShowUiModel(
        id = 1,
        name = "Breaking Bad",
        overview = "A high school chemistry teacher turned methamphetamine manufacturer.",
        firstAirDate = "20 Jan 2008",
        voteAverage = "9.5",
        backdropUrl = null,
        posterUrl = null,
    )

private val previewTvShowDetail =
    TvShowDetailUiModel(
        id = 1,
        name = "Breaking Bad",
        overview = "A high school chemistry teacher turned methamphetamine manufacturer.",
        tagline = "Remember my name.",
        firstAirDate = "20 Jan 2008",
        lastAirDate = "29 Sep 2013",
        yearsRange = "2008 – 2013",
        voteAverage = "9.5",
        voteCount = "15,000",
        backdropUrl = null,
        posterUrl = null,
        seasonsLabel = "5 seasons · 62 episodes",
        genres = "Drama, Crime",
        networks = "AMC",
        seasons =
            listOf(
                SeasonUiModel(1, 1, "Season 1", "2008", "7 episodes", null),
                SeasonUiModel(2, 2, "Season 2", "2009", "13 episodes", null),
            ),
    )

@PreviewLightDark
@Composable
private fun TvShowDetailLoadingPreview() {
    SmoovieTheme {
        TvShowDetailContent(
            tvShow = previewTvShow,
            detailState = TvShowDetailUiState.Loading,
            onBack = {},
            onRetry = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun TvShowDetailSuccessPreview() {
    SmoovieTheme {
        TvShowDetailContent(
            tvShow = previewTvShow,
            detailState = TvShowDetailUiState.Success(previewTvShowDetail),
            onBack = {},
            onRetry = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun TvShowDetailErrorPreview() {
    SmoovieTheme {
        TvShowDetailContent(
            tvShow = previewTvShow,
            detailState = TvShowDetailUiState.Error(AppError.NetworkError),
            onBack = {},
            onRetry = {},
        )
    }
}

// endregion
