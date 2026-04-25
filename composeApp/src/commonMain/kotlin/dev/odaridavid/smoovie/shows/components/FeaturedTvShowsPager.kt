package dev.odaridavid.smoovie.shows.components

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import dev.odaridavid.smoovie.shows.TvShowUiModel
import dev.odaridavid.smoovie.theme.SmoovieTheme
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import smoovie.composeapp.generated.resources.Res
import smoovie.composeapp.generated.resources.search_shows_hint

private val PAGER_HEIGHT = 260.dp
private val ICON_SCRIM_COLOR = Color.Black.copy(alpha = 0.35f)

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun FeaturedTvShowsPager(
    tvShows: List<TvShowUiModel>,
    onSearchClick: () -> Unit,
    onTvShowClick: (TvShowUiModel) -> Unit,
) {
    if (tvShows.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { tvShows.size })

    LaunchedEffect(Unit) {
        while (true) {
            delay(4_000)
            pagerState.animateScrollToPage(
                page = (pagerState.currentPage + 1) % tvShows.size,
                animationSpec = tween(durationMillis = 600, easing = EaseInOut),
            )
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(PAGER_HEIGHT),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            val show = tvShows[page]
            FeaturedPage(tvShow = show, onClick = { onTvShowClick(show) })
        }

        Box(
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        Brush.verticalGradient(
                            colors =
                                listOf(
                                    Color.Black.copy(alpha = 0.55f),
                                    Color.Transparent,
                                ),
                        ),
                    ),
        )

        Box(
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(
                        Brush.verticalGradient(
                            colors =
                                listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.8f),
                                ),
                        ),
                    ),
        )

        Row(
            modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(end = 4.dp),
        ) {
            IconButton(
                onClick = onSearchClick,
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White),
                modifier = Modifier.background(ICON_SCRIM_COLOR, CircleShape),
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(Res.string.search_shows_hint),
                )
            }
        }

        // Pagination dots — bottom padding clears the 28dp sheet overlap from the screen
        Row(
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 36.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(tvShows.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier =
                        Modifier
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
    tvShow: TvShowUiModel,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .clickable { onClick() },
    ) {
        val imageUrl = tvShow.backdropUrl ?: tvShow.posterUrl
        if (imageUrl != null) {
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                loading = { FeaturedPlaceholder() },
                error = { FeaturedPlaceholder() },
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            FeaturedPlaceholder()
        }
    }
}

@Composable
private fun FeaturedPlaceholder() {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Tv,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@PreviewLightDark
@Composable
private fun FeaturedTvShowsPagerPreview() {
    SmoovieTheme {
        FeaturedTvShowsPager(
            tvShows =
                listOf(
                    TvShowUiModel(1, "Breaking Bad", "", "2008", "9.5", null, null),
                    TvShowUiModel(2, "Stranger Things", "", "2016", "8.7", null, null),
                ),
            onSearchClick = {},
            onTvShowClick = {},
        )
    }
}
