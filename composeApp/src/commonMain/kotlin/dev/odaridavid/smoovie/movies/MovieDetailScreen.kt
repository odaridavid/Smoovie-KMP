package dev.odaridavid.smoovie.movies

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import dev.odaridavid.smoovie.theme.SmoovieTheme
import org.jetbrains.compose.resources.stringResource
import smoovie.composeapp.generated.resources.Res
import smoovie.composeapp.generated.resources.action_retry
import smoovie.composeapp.generated.resources.error_movie_detail_failed
import previewMovieDetailUiModel
import previewMovieUiModels
import smoovie.composeapp.generated.resources.navigate_back

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MovieDetailContent(
    movie: MovieUiModel,
    detailState: MovieDetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
) {
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = movie.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(Res.string.navigate_back),
                            )
                        }
                    },
                )
                AnimatedVisibility(visible = detailState is MovieDetailUiState.Loading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
        ) {
            BackdropImage(
                backdropUrl = movie.backdropUrl,
                posterUrl = movie.posterUrl,
            )

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                TitleSection(movie = movie, detailState = detailState)
                MetadataSection(movie = movie, detailState = detailState)

                if (detailState is MovieDetailUiState.Error) {
                    ErrorBanner(onRetry = onRetry)
                }

                val overview = when (detailState) {
                    is MovieDetailUiState.Success -> detailState.movieDetail.overview
                    else -> movie.overview
                }
                if (overview.isNotBlank()) {
                    Text(
                        text = overview,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Composable
private fun BackdropImage(
    backdropUrl: String?,
    posterUrl: String?,
) {
    val imageUrl = backdropUrl ?: posterUrl
    if (imageUrl != null) {
        SubcomposeAsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            loading = { BackdropPlaceholder() },
            error = { BackdropPlaceholder() },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
        )
    } else {
        BackdropPlaceholder()
    }
}

@Composable
private fun BackdropPlaceholder() {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Movie,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun TitleSection(
    movie: MovieUiModel,
    detailState: MovieDetailUiState,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = movie.title,
            style = MaterialTheme.typography.headlineSmall,
        )
        val tagline = (detailState as? MovieDetailUiState.Success)?.movieDetail?.tagline
        if (!tagline.isNullOrBlank()) {
            Text(
                text = tagline,
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MetadataSection(
    movie: MovieUiModel,
    detailState: MovieDetailUiState,
) {
    val detail = (detailState as? MovieDetailUiState.Success)?.movieDetail

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (movie.voteAverage.isNotBlank()) {
            AssistChip(
                onClick = {},
                label = { Text("★ ${movie.voteAverage}") },
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
