package dev.odaridavid.smoovie.shows.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import dev.odaridavid.smoovie.shows.TvShowUiModel
import dev.odaridavid.smoovie.theme.SmoovieTheme
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import smoovie.composeapp.generated.resources.Res
import smoovie.composeapp.generated.resources.no_description_available
import smoovie.composeapp.generated.resources.release_date_tba
import smoovie.composeapp.generated.resources.unrated

private val IMAGE_HEIGHT = 140.dp
private const val CARD_ANIM_DURATION = 350
private const val CARD_STAGGER_MS = 60
private const val CARD_STAGGER_LIMIT = 5

@Composable
internal fun TvShowCard(
    tvShow: TvShowUiModel,
    onClick: () -> Unit,
) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.height(IMAGE_HEIGHT)) {
            SubcomposeAsyncImage(
                model = tvShow.posterUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                loading = { ImagePlaceholder() },
                error = { ImagePlaceholder() },
                modifier =
                    Modifier
                        .width(96.dp)
                        .fillMaxHeight(),
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
                    Title(tvShow.name)
                    Overview(tvShow.overview)
                }
                Footer(tvShow)
            }
        }
    }
}

@Composable
internal fun AnimatedTvShowCard(
    tvShow: TvShowUiModel,
    index: Int,
    skipAnimation: Boolean,
    onAnimationEnd: () -> Unit,
    onClick: () -> Unit,
) {
    if (index >= CARD_STAGGER_LIMIT || skipAnimation) {
        TvShowCard(tvShow = tvShow, onClick = onClick)
        return
    }
    var entered by remember { mutableStateOf(false) }
    val staggerDelay = index * CARD_STAGGER_MS
    LaunchedEffect(Unit) {
        entered = true
        delay((staggerDelay + CARD_ANIM_DURATION).toLong())
        onAnimationEnd()
    }
    val spec = tween<Float>(CARD_ANIM_DURATION, delayMillis = staggerDelay)
    val scaleX by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = spec,
        label = "scaleX",
    )
    val alpha by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = spec,
        label = "alpha",
    )
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    this.scaleX = scaleX
                    this.alpha = alpha
                },
    ) {
        TvShowCard(tvShow = tvShow, onClick = onClick)
    }
}

@Composable
private fun Footer(tvShow: TvShowUiModel) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text =
                if (tvShow.voteAverage.isNotBlank()) {
                    "★ ${tvShow.voteAverage}"
                } else {
                    "★ ${stringResource(Res.string.unrated)}"
                },
            style = MaterialTheme.typography.labelLarge,
        )
        Text(
            text =
                tvShow.firstAirDate.ifBlank {
                    stringResource(Res.string.release_date_tba)
                },
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
private fun Overview(overview: String) {
    if (overview.isNotBlank()) {
        Text(
            text = overview,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis,
        )
    } else {
        Text(
            text = stringResource(Res.string.no_description_available),
            style = MaterialTheme.typography.bodySmall,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun Title(name: String) {
    Text(
        text = name,
        style = MaterialTheme.typography.titleMedium,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun ImagePlaceholder() {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.tertiaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Tv,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@PreviewLightDark
@Composable
private fun TvShowCardPreview() {
    SmoovieTheme {
        TvShowCard(
            tvShow =
                TvShowUiModel(
                    id = 1,
                    name = "Breaking Bad",
                    overview = "A high school chemistry teacher turned methamphetamine manufacturer.",
                    firstAirDate = "20 Jan 2008",
                    voteAverage = "9.5",
                    backdropUrl = null,
                    posterUrl = null,
                ),
            onClick = {},
        )
    }
}
