package dev.odaridavid.smoovie.movies

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
import dev.odaridavid.smoovie.movies.components.SimilarMoviesSection
import dev.odaridavid.smoovie.movies.components.TrailersSection
import dev.odaridavid.smoovie.movies.components.WhereToWatchSection
import dev.odaridavid.smoovie.person.PersonSummaryUiModel
import dev.odaridavid.smoovie.theme.ExpandableText
import dev.odaridavid.smoovie.theme.HeroSection
import dev.odaridavid.smoovie.theme.KeywordChips
import dev.odaridavid.smoovie.theme.MetadataRow
import dev.odaridavid.smoovie.theme.ShimmerDetail
import dev.odaridavid.smoovie.theme.SmoovieTheme
import dev.odaridavid.smoovie.ui.SetStatusBarIcons
import dev.odaridavid.smoovie.utils.AppError
import dev.odaridavid.smoovie.utils.previewMovieDetailUiModel
import dev.odaridavid.smoovie.utils.previewMovieUiModels
import org.jetbrains.compose.resources.stringResource
import smoovie.composeapp.generated.resources.Res
import smoovie.composeapp.generated.resources.action_retry
import smoovie.composeapp.generated.resources.error_network
import smoovie.composeapp.generated.resources.error_not_found
import smoovie.composeapp.generated.resources.error_server
import smoovie.composeapp.generated.resources.error_unauthorized
import smoovie.composeapp.generated.resources.error_unknown

@Composable
fun MovieDetailScreen(
    viewModel: MovieDetailViewModel,
    movie: MovieUiModel,
    onBack: () -> Unit,
    onMovieClick: (MovieUiModel) -> Unit,
    onPersonClick: (PersonSummaryUiModel) -> Unit,
) {
    val detailState by viewModel.uiState.collectAsState()
    val isInWatchlist by viewModel.isInWatchlist.collectAsState()
    MovieDetailContent(
        movie = movie,
        detailState = detailState,
        isInWatchlist = isInWatchlist,
        onBack = onBack,
        onRetry = viewModel::loadMovieDetail,
        onToggleWatchlist = { viewModel.toggleWatchlist(movie) },
        onMovieClick = onMovieClick,
        onPersonClick = onPersonClick,
    )
}

@Composable
internal fun MovieDetailContent(
    movie: MovieUiModel,
    detailState: MovieDetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    isInWatchlist: Boolean = false,
    onToggleWatchlist: () -> Unit = {},
    onMovieClick: (MovieUiModel) -> Unit = {},
    onPersonClick: (PersonSummaryUiModel) -> Unit = {},
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
                backdropUrl = movie.backdropUrl,
                posterUrl = movie.posterUrl,
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
                title = movie.title,
                tagline = (detailState as? MovieDetailUiState.Success)?.movieDetail?.tagline,
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            when (detailState) {
                is MovieDetailUiState.Loading -> {
                    ShimmerDetail(modifier = Modifier.fillMaxWidth())
                }

                is MovieDetailUiState.Success -> {
                    val detail = detailState.movieDetail
                    DetailBody(movie = movie, detail = detail)
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
                        SimilarMoviesSection(
                            movies = detail.similar,
                            onMovieClick = onMovieClick,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                        )
                    }
                }

                is MovieDetailUiState.Error -> {
                    DetailBody(movie = movie) {
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
    movie: MovieUiModel,
    detail: MovieDetailUiModel? = null,
    extraContent: @Composable (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        MetadataSection(movie = movie, detail = detail)

        if (!detail?.keywords.isNullOrEmpty()) {
            KeywordChips(keywords = detail.keywords)
        }

        extraContent?.invoke()

        val overview = detail?.overview ?: movie.overview
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
    movie: MovieUiModel,
    detail: MovieDetailUiModel? = null,
) {
    val rating =
        if (movie.voteAverage.isNotBlank()) {
            val voteCount = detail?.voteCount
            if (!voteCount.isNullOrBlank()) "★ ${movie.voteAverage} ($voteCount)" else "★ ${movie.voteAverage}"
        } else {
            null
        }
    MetadataRow(
        rating,
        detail?.runtime,
        movie.releaseDate.takeIf { it.isNotBlank() },
        detail?.genres,
        detail?.ageRating,
        detail?.director?.takeIf { it.isNotBlank() }?.let { "🎬 $it" },
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

@PreviewLightDark
@Composable
private fun MovieDetailLoadingPreview() {
    SmoovieTheme {
        MovieDetailContent(
            movie = previewMovieUiModels[0],
            detailState = MovieDetailUiState.Loading,
            onBack = {},
            onRetry = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun MovieDetailSuccessPreview() {
    SmoovieTheme {
        MovieDetailContent(
            movie = previewMovieUiModels[0],
            detailState = MovieDetailUiState.Success(previewMovieDetailUiModel),
            onBack = {},
            onRetry = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun MovieDetailErrorPreview() {
    SmoovieTheme {
        MovieDetailContent(
            movie = previewMovieUiModels[0],
            detailState = MovieDetailUiState.Error(AppError.NetworkError),
            onBack = {},
            onRetry = {},
        )
    }
}

// endregion
