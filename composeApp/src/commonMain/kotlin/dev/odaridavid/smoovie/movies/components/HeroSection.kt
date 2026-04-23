package dev.odaridavid.smoovie.movies.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import dev.odaridavid.smoovie.movies.MovieUiModel
import dev.odaridavid.smoovie.theme.SmoovieTheme
import org.jetbrains.compose.resources.stringResource
import previewMovieUiModels
import smoovie.composeapp.generated.resources.Res
import smoovie.composeapp.generated.resources.navigate_back
import smoovie.composeapp.generated.resources.watchlist_add_content_description
import smoovie.composeapp.generated.resources.watchlist_remove_content_description

@Composable
internal fun HeroSection(
    movie: MovieUiModel,
    onBack: () -> Unit,
    isInWatchlist: Boolean = false,
    onToggleWatchlist: () -> Unit = {},
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        BackdropImage(
            backdropUrl = movie.backdropUrl,
            posterUrl = movie.posterUrl,
        )

        BackButtonScrim()

        BackButton(onBack)

        WatchlistToggle(isInWatchlist = isInWatchlist, onToggle = onToggleWatchlist)
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
            modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f),
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
private fun HeroSectionPreview() {
    SmoovieTheme {
        HeroSection(
            movie = previewMovieUiModels[0],
            onBack = {},
        )
    }
}

// endregion
