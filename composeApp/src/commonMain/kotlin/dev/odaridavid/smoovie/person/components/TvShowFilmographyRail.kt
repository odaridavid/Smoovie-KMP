package dev.odaridavid.smoovie.person.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import dev.odaridavid.smoovie.person.PersonTvFilmographyItem
import dev.odaridavid.smoovie.shows.TvShowUiModel

private val POSTER_WIDTH = 120.dp
private const val POSTER_ASPECT_RATIO = 2f / 3f
private const val RAIL_LIMIT = 6

@Composable
internal fun TvShowFilmographyRail(
    title: String,
    items: List<PersonTvFilmographyItem>,
    onTvShowClick: (TvShowUiModel) -> Unit,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (items.isEmpty()) return
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        RailHeader(
            title = title,
            showViewAll = items.size > RAIL_LIMIT,
            onViewAll = onViewAll,
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(items.take(RAIL_LIMIT), key = { it.tvShow.id }) { item ->
                TvShowPosterTile(item = item, onClick = { onTvShowClick(item.tvShow) })
            }
        }
    }
}

@Composable
private fun TvShowPosterTile(
    item: PersonTvFilmographyItem,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier.width(POSTER_WIDTH).clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(POSTER_ASPECT_RATIO)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            SubcomposeAsyncImage(
                model = item.tvShow.posterUrl,
                contentDescription = item.tvShow.name,
                contentScale = ContentScale.Crop,
                loading = { PosterPlaceholder() },
                error = { PosterPlaceholder() },
                modifier = Modifier.fillMaxSize(),
            )
            TvBadge(
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp),
            )
        }
        Text(
            text = item.tvShow.name,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        if (item.role.isNotBlank()) {
            Text(
                text = item.role,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun PosterPlaceholder() {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Tv,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
