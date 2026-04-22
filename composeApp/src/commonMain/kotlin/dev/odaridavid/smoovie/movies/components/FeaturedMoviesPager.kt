package dev.odaridavid.smoovie.movies.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import dev.odaridavid.smoovie.ui.LocalAnimatedVisibilityScope
import dev.odaridavid.smoovie.ui.LocalSharedTransitionScope
import kotlinx.coroutines.delay
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import smoovie.composeapp.generated.resources.search_movies_hint
import smoovie.composeapp.generated.resources.watchlist_open_content_description

private val PAGER_HEIGHT = 340.dp
private val ICON_SCRIM_COLOR = Color.Black.copy(alpha = 0.35f)

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun FeaturedMoviesPager(
    movies: List<MovieUiModel>,
    onSearchClick: () -> Unit,
    onWatchlistClick: () -> Unit,
    onMovieClick: (MovieUiModel) -> Unit,
) {
    if (movies.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { movies.size })

    LaunchedEffect(Unit) {
        while (true) {
            delay(4_000)
            pagerState.animateScrollToPage(
                page = (pagerState.currentPage + 1) % movies.size,
                animationSpec = tween(durationMillis = 600, easing = EaseInOut),
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(PAGER_HEIGHT),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            val movie = movies[page]
            FeaturedPage(movie = movie, onClick = { onMovieClick(movie) })
        }

        // Top scrim
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(100.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.55f),
                            Color.Transparent,
                        ),
                    ),
                ),
        )

        // Bottom scrim
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(140.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.8f),
                        ),
                    ),
                ),
        )

        // Top-right action row — watchlist + search, below status bar
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(end = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            IconButton(
                onClick = onWatchlistClick,
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White),
                modifier = Modifier.background(ICON_SCRIM_COLOR, CircleShape),
            ) {
                Icon(
                    imageVector = Icons.Default.BookmarkBorder,
                    contentDescription = stringResource(Res.string.watchlist_open_content_description),
                )
            }
            IconButton(
                onClick = onSearchClick,
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White),
                modifier = Modifier.background(ICON_SCRIM_COLOR, CircleShape),
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(Res.string.search_movies_hint),
                )
            }
        }

        // Movie title
        Text(
            text = movies[pagerState.currentPage].title,
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, end = 72.dp, bottom = 36.dp),
        )

        // Pagination dots
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(movies.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 8.dp else 5.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) Color.White else Color.White.copy(alpha = 0.4f),
                        ),
                )
            }
        }
    }
}

@Composable
private fun FeaturedPage(
    movie: MovieUiModel,
    onClick: () -> Unit,
) {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedVisibilityScope = LocalAnimatedVisibilityScope.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onClick() },
    ) {
        val imageUrl = movie.backdropUrl ?: movie.posterUrl
        if (imageUrl != null) {
            val imageModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                with(sharedTransitionScope) {
                    Modifier.sharedElement(
                        sharedContentState = rememberSharedContentState(key = "hero_image_${movie.id}"),
                        animatedVisibilityScope = animatedVisibilityScope,
                    ).fillMaxSize()
                }
            } else {
                Modifier.fillMaxSize()
            }
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                loading = { FeaturedPlaceholder() },
                error = { FeaturedPlaceholder() },
                modifier = imageModifier,
            )
        } else {
            FeaturedPlaceholder()
        }
    }
}

@Composable
private fun FeaturedPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
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

@PreviewLightDark
@Composable
private fun FeaturedMoviesPagerPreview() {
    SmoovieTheme {
        FeaturedMoviesPager(
            movies = previewMovieUiModels.take(4),
            onSearchClick = {},
            onWatchlistClick = {},
            onMovieClick = {},
        )
    }
}
