package dev.odaridavid.smoovie.movies.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import dev.odaridavid.smoovie.ui.LocalAnimatedVisibilityScope
import dev.odaridavid.smoovie.ui.LocalSharedTransitionScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import dev.odaridavid.smoovie.movies.MovieDetailUiState
import dev.odaridavid.smoovie.movies.MovieUiModel
import dev.odaridavid.smoovie.theme.SmoovieTheme
import org.jetbrains.compose.resources.stringResource
import previewMovieDetailUiModel
import previewMovieUiModels
import smoovie.composeapp.generated.resources.Res
import smoovie.composeapp.generated.resources.navigate_back
import smoovie.composeapp.generated.resources.watchlist_add_content_description
import smoovie.composeapp.generated.resources.watchlist_remove_content_description

@Composable
internal fun HeroSection(
    movie: MovieUiModel,
    detailState: MovieDetailUiState,
    onBack: () -> Unit,
    isInWatchlist: Boolean = false,
    onToggleWatchlist: () -> Unit = {},
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        BackdropImage(
            movieId = movie.id,
            backdropUrl = movie.backdropUrl,
            posterUrl = movie.posterUrl,
        )

        // Top scrim for back button
        BackButtonScrim()

        // Bottom scrim for title
        TitleScrim()

        // Back button
        BackButton(onBack)

        // Watchlist toggle
        WatchlistToggle(isInWatchlist = isInWatchlist, onToggle = onToggleWatchlist)

        // Title on bottom scrim
        Column(
            modifier =
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
            )
            val tagline = (detailState as? MovieDetailUiState.Success)?.movieDetail?.tagline
            if (!tagline.isNullOrBlank()) {
                Text(
                    text = tagline,
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = Color.White.copy(alpha = 0.8f),
                )
            }
        }
    }
}

@Composable
private fun BoxScope.WatchlistToggle(
    isInWatchlist: Boolean,
    onToggle: () -> Unit,
) {
    IconButton(
        onClick = onToggle,
        colors =
            IconButtonDefaults.iconButtonColors(
                contentColor = Color.White,
            ),
        modifier =
            Modifier
                .align(Alignment.TopEnd)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(4.dp)
                .background(SCRIM_COLOR, CircleShape),
    ) {
        Icon(
            imageVector =
                if (isInWatchlist) {
                    Icons.Default.Bookmark
                } else {
                    Icons.Default.BookmarkBorder
                },
            contentDescription =
                stringResource(
                    if (isInWatchlist) {
                        Res.string.watchlist_remove_content_description
                    } else {
                        Res.string.watchlist_add_content_description
                    },
                ),
        )
    }
}

@Composable
private fun BoxScope.BackButton(onBack: () -> Unit) {
    IconButton(
        onClick = onBack,
        colors =
            IconButtonDefaults.iconButtonColors(
                contentColor = Color.White,
            ),
        modifier =
            Modifier
                .align(Alignment.TopStart)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(4.dp)
                .background(SCRIM_COLOR, CircleShape),
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(Res.string.navigate_back),
        )
    }
}

private val SCRIM_COLOR = Color.Black.copy(alpha = 0.35f)

@Composable
private fun BoxScope.TitleScrim() {
    Box(
        modifier =
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    Brush.verticalGradient(
                        colors =
                            listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f),
                            ),
                    ),
                ),
    )
}

@Composable
private fun BoxScope.BackButtonScrim() {
    Box(
        modifier =
            Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(80.dp)
                .background(
                    Brush.verticalGradient(
                        colors =
                            listOf(
                                Color.Black.copy(alpha = 0.6f),
                                Color.Transparent,
                            ),
                    ),
                ),
    )
}

@Composable
private fun BackdropImage(
    movieId: Int,
    backdropUrl: String?,
    posterUrl: String?,
) {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedVisibilityScope = LocalAnimatedVisibilityScope.current
    val imageUrl = backdropUrl ?: posterUrl
    if (imageUrl != null) {
        val imageModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
            with(sharedTransitionScope) {
                Modifier.sharedElement(
                    sharedContentState = rememberSharedContentState(key = "hero_image_$movieId"),
                    animatedVisibilityScope = animatedVisibilityScope,
                ).fillMaxWidth().aspectRatio(16f / 9f)
            }
        } else {
            Modifier.fillMaxWidth().aspectRatio(16f / 9f)
        }
        SubcomposeAsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            loading = { BackdropPlaceholder() },
            error = { BackdropPlaceholder() },
            modifier = imageModifier,
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

// region Previews

@PreviewLightDark
@Composable
private fun HeroSectionLoadingPreview() {
    SmoovieTheme {
        HeroSection(
            movie = previewMovieUiModels[0],
            detailState = MovieDetailUiState.Loading,
            onBack = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun HeroSectionWithTaglinePreview() {
    SmoovieTheme {
        HeroSection(
            movie = previewMovieUiModels[0],
            detailState = MovieDetailUiState.Success(previewMovieDetailUiModel),
            onBack = {},
        )
    }
}

// endregion
