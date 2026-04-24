package dev.odaridavid.smoovie.shows.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import dev.odaridavid.smoovie.shows.EpisodeUiModel
import dev.odaridavid.smoovie.theme.ExpandableText
import dev.odaridavid.smoovie.theme.SmoovieTheme

private val STILL_WIDTH = 128.dp

@Composable
internal fun EpisodeItem(episode: EpisodeUiModel) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StillImage(url = episode.stillUrl)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = episode.headerLabel,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    val metadata =
                        listOf(episode.airDate, episode.runtimeLabel)
                            .filter { it.isNotBlank() }
                            .joinToString(" · ")
                    if (metadata.isNotBlank()) {
                        Text(
                            text = metadata,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (episode.voteAverage.isNotBlank()) {
                        Text(
                            text = "★ ${episode.voteAverage}",
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                }
            }
            if (episode.overview.isNotBlank()) {
                ExpandableText(
                    text = episode.overview,
                    style = MaterialTheme.typography.bodySmall,
                    collapsedMaxLines = 3,
                )
            }
        }
    }
}

@Composable
private fun StillImage(url: String?) {
    Box(
        modifier =
            Modifier
                .width(STILL_WIDTH)
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        SubcomposeAsyncImage(
            model = url,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            loading = { StillPlaceholder() },
            error = { StillPlaceholder() },
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun StillPlaceholder() {
    Icon(
        imageVector = Icons.Default.Tv,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@PreviewLightDark
@Composable
private fun EpisodeItemPreview() {
    SmoovieTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            EpisodeItem(
                episode =
                    EpisodeUiModel(
                        id = 1,
                        episodeNumber = 1,
                        name = "Pilot",
                        overview = "Walter White's transformation begins.",
                        airDate = "20 Jan 2008",
                        runtimeLabel = "58 min",
                        voteAverage = "8.3",
                        stillUrl = null,
                        headerLabel = "Ep 1 · Pilot",
                    ),
            )
        }
    }
}
