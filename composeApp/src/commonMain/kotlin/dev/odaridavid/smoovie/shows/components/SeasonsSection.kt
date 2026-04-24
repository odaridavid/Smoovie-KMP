package dev.odaridavid.smoovie.shows.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import dev.odaridavid.smoovie.shows.SeasonUiModel
import dev.odaridavid.smoovie.theme.SmoovieTheme
import org.jetbrains.compose.resources.stringResource
import smoovie.composeapp.generated.resources.Res
import smoovie.composeapp.generated.resources.tv_seasons_section_title

private val POSTER_WIDTH = 120.dp
private const val POSTER_ASPECT_RATIO = 2f / 3f

@Composable
internal fun SeasonsSection(
    seasons: List<SeasonUiModel>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(Res.string.tv_seasons_section_title),
            style = MaterialTheme.typography.titleMedium,
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(seasons, key = { it.id }) { season ->
                SeasonItem(season)
            }
        }
    }
}

@Composable
private fun SeasonItem(season: SeasonUiModel) {
    Column(
        modifier = Modifier.width(POSTER_WIDTH),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(POSTER_ASPECT_RATIO)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            SubcomposeAsyncImage(
                model = season.posterUrl,
                contentDescription = season.name,
                contentScale = ContentScale.Crop,
                loading = { PosterPlaceholder() },
                error = { PosterPlaceholder() },
                modifier = Modifier.fillMaxSize(),
            )
        }
        Text(
            text = season.name,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        val subtitle =
            listOf(season.year, season.episodeCountLabel)
                .filter { it.isNotBlank() }
                .joinToString(" · ")
        if (subtitle.isNotBlank()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun PosterPlaceholder() {
    Icon(
        imageVector = Icons.Default.Tv,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@PreviewLightDark
@Composable
private fun SeasonsSectionPreview() {
    SmoovieTheme {
        SeasonsSection(
            seasons =
                listOf(
                    SeasonUiModel(1, "Season 1", "2008", "7 episodes", null),
                    SeasonUiModel(2, "Season 2", "2009", "13 episodes", null),
                    SeasonUiModel(3, "Season 3", "2010", "13 episodes", null),
                ),
        )
    }
}
