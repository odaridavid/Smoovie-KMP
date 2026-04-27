package dev.odaridavid.smoovie.theme

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import smoovie.composeapp.generated.resources.Res
import smoovie.composeapp.generated.resources.filter_button_description
import smoovie.composeapp.generated.resources.search_movies_hint

private const val SHIMMER_ITEM_COUNT = 5
private val PAGER_SHIMMER_HEIGHT = 260.dp
private val ICON_SCRIM_COLOR = Color.Black.copy(alpha = 0.35f)

@Composable
internal fun ShimmerHero(
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit = {},
    isFilterActive: Boolean = false,
) {
    ShimmerFeaturedHero(
        brush = rememberShimmerBrush(),
        onSearchClick = onSearchClick,
        onFilterClick = onFilterClick,
        isFilterActive = isFilterActive,
    )
}

@Composable
internal fun ShimmerList(modifier: Modifier = Modifier) {
    val shimmerBrush = rememberShimmerBrush()
    Column(modifier = modifier) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            userScrollEnabled = false,
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
        ) {
            items(SHIMMER_ITEM_COUNT) {
                ShimmerMovieCard(brush = shimmerBrush)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShimmerFeaturedHero(
    brush: Brush,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit = {},
    isFilterActive: Boolean = false,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(PAGER_SHIMMER_HEIGHT)
                .background(brush),
    ) {
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
                    contentDescription = stringResource(Res.string.search_movies_hint),
                )
            }
            IconButton(
                onClick = onFilterClick,
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White),
                modifier = Modifier.background(ICON_SCRIM_COLOR, CircleShape),
            ) {
                BadgedBox(badge = { if (isFilterActive) Badge() }) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = stringResource(Res.string.filter_button_description),
                    )
                }
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
            repeat(4) { index ->
                Box(
                    modifier =
                        Modifier
                            .size(if (index == 0) 8.dp else 5.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = if (index == 0) 0.35f else 0.2f)),
                )
            }
        }
    }
}

@Composable
internal fun rememberShimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(durationMillis = 1200),
                repeatMode = RepeatMode.Restart,
            ),
        label = "shimmer_translate",
    )
    val shimmerColors =
        listOf(
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f),
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        )
    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 200f, translateAnim - 200f),
        end = Offset(translateAnim, translateAnim),
    )
}

@Composable
private fun ShimmerMovieCard(brush: Brush) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.height(140.dp)) {
            Box(
                modifier =
                    Modifier
                        .width(96.dp)
                        .fillMaxHeight()
                        .background(brush),
            )
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth(0.7f)
                                .height(18.dp)
                                .background(brush, MaterialTheme.shapes.small),
                    )
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .background(brush, MaterialTheme.shapes.small),
                    )
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth(0.9f)
                                .height(12.dp)
                                .background(brush, MaterialTheme.shapes.small),
                    )
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth(0.6f)
                                .height(12.dp)
                                .background(brush, MaterialTheme.shapes.small),
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(
                        modifier =
                            Modifier
                                .width(60.dp)
                                .height(14.dp)
                                .background(brush, MaterialTheme.shapes.small),
                    )
                    Box(
                        modifier =
                            Modifier
                                .width(80.dp)
                                .height(14.dp)
                                .background(brush, MaterialTheme.shapes.small),
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun ShimmerListPreview() {
    SmoovieTheme {
        ShimmerList(modifier = Modifier.fillMaxSize())
    }
}
