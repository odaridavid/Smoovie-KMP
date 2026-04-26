package dev.odaridavid.smoovie.movies.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import dev.odaridavid.smoovie.movies.TrailerUiModel
import dev.odaridavid.smoovie.theme.SmoovieTheme
import org.jetbrains.compose.resources.stringResource
import dev.odaridavid.smoovie.utils.previewTrailers
import smoovie.composeapp.generated.resources.Res
import smoovie.composeapp.generated.resources.trailers_play_content_description
import smoovie.composeapp.generated.resources.trailers_section_title

@Composable
internal fun TrailersSection(
    trailers: List<TrailerUiModel>,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(Res.string.trailers_section_title),
            style = MaterialTheme.typography.titleMedium,
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(trailers, key = { it.id }) { trailer ->
                TrailerItem(
                    trailer = trailer,
                    onClick = { uriHandler.openUri("https://www.youtube.com/watch?v=${trailer.videoKey}") },
                )
            }
        }
    }
}

@Composable
private fun TrailerItem(
    trailer: TrailerUiModel,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier.width(TRAILER_WIDTH),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(THUMBNAIL_ASPECT_RATIO)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            SubcomposeAsyncImage(
                model = trailer.thumbnailUrl,
                contentDescription = trailer.name,
                contentScale = ContentScale.Crop,
                loading = {},
                error = {},
                modifier = Modifier.fillMaxSize(),
            )
            PlayBadge()
        }
        if (trailer.name.isNotBlank()) {
            Text(
                text = trailer.name,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun PlayBadge() {
    Box(
        modifier =
            Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.55f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.PlayArrow,
            contentDescription = stringResource(Res.string.trailers_play_content_description),
            tint = Color.White,
            modifier = Modifier.padding(start = 2.dp).size(24.dp),
        )
    }
}

private val TRAILER_WIDTH = 220.dp
private const val THUMBNAIL_ASPECT_RATIO = 16f / 9f

// region Previews

@PreviewLightDark
@Composable
private fun TrailersSectionPreview() {
    SmoovieTheme {
        TrailersSection(
            trailers = previewTrailers,
            modifier = Modifier.padding(16.dp),
        )
    }
}

// endregion
