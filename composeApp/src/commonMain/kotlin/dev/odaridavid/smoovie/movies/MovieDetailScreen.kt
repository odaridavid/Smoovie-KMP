package dev.odaridavid.smoovie.movies

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import dev.odaridavid.smoovie.theme.SmoovieTheme
import org.jetbrains.compose.resources.stringResource
import previewMovieDetailUiModel
import previewMovieUiModels
import smoovie.composeapp.generated.resources.Res
import smoovie.composeapp.generated.resources.action_retry
import smoovie.composeapp.generated.resources.error_movie_detail_failed

@Composable
fun MovieDetailScreen(
    viewModel: MovieDetailViewModel,
    movie: MovieUiModel,
    onBack: () -> Unit,
) {
    val detailState by viewModel.uiState.collectAsState()
    MovieDetailContent(
        movie = movie,
        detailState = detailState,
        onBack = onBack,
        onRetry = viewModel::loadMovieDetail,
    )
}

@Composable
internal fun MovieDetailContent(
    movie: MovieUiModel,
    detailState: MovieDetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.navigationBars),
    ) {
        HeroSection(movie = movie, detailState = detailState, onBack = onBack)

        when (detailState) {
            is MovieDetailUiState.Loading -> {
                ShimmerMovieDetail(modifier = Modifier.fillMaxWidth())
            }

            is MovieDetailUiState.Success -> {
                val detail = detailState.movieDetail
                DetailBody(movie = movie, detail = detail)
                if (detail.cast.isNotEmpty()) {
                    CastSection(
                        cast = detail.cast,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }

            is MovieDetailUiState.Error -> {
                DetailBody(movie = movie) {
                    ErrorBanner(onRetry = onRetry)
                }
            }
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

        extraContent?.invoke()

        val overview = detail?.overview ?: movie.overview
        if (overview.isNotBlank()) {
            Text(
                text = overview,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MetadataSection(
    movie: MovieUiModel,
    detail: MovieDetailUiModel? = null,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (movie.voteAverage.isNotBlank()) {
            val voteCount = detail?.voteCount
            val ratingLabel =
                if (!voteCount.isNullOrBlank()) {
                    "★ ${movie.voteAverage} ($voteCount)"
                } else {
                    "★ ${movie.voteAverage}"
                }
            AssistChip(
                onClick = {},
                label = { Text(ratingLabel) },
            )
        }
        val runtime = detail?.runtime
        if (!runtime.isNullOrBlank()) {
            AssistChip(
                onClick = {},
                label = { Text(runtime) },
            )
        }
        if (movie.releaseDate.isNotBlank()) {
            AssistChip(
                onClick = {},
                label = { Text(movie.releaseDate) },
            )
        }
        val genres = detail?.genres
        if (!genres.isNullOrBlank()) {
            AssistChip(
                onClick = {},
                label = { Text(genres) },
            )
        }
        val director = detail?.director
        if (!director.isNullOrBlank()) {
            AssistChip(
                onClick = {},
                label = { Text("🎬 $director") },
            )
        }
    }
}

@Composable
private fun ErrorBanner(onRetry: () -> Unit) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.errorContainer,
                    MaterialTheme.shapes.small,
                )
                .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            text = stringResource(Res.string.error_movie_detail_failed),
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
            detailState = MovieDetailUiState.Error("Network error"),
            onBack = {},
            onRetry = {},
        )
    }
}

// endregion