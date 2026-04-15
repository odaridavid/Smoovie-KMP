package dev.odaridavid.smoovie.movies

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
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import dev.odaridavid.smoovie.theme.SmoovieTheme
import org.jetbrains.compose.resources.stringResource
import previewMovieUiModels
import smoovie.composeapp.generated.resources.Res
import smoovie.composeapp.generated.resources.no_description_available
import smoovie.composeapp.generated.resources.release_date_tba
import smoovie.composeapp.generated.resources.unrated

private val IMAGE_HEIGHT = 140.dp

@Composable
internal fun MovieCard(
    movie: MovieUiModel,
    onClick: () -> Unit,
) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.height(IMAGE_HEIGHT)) {
            SubcomposeAsyncImage(
                model = movie.posterUrl,
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
                    MovieTitle(movie.title)
                    MovieOverview(movie.overview)
                }
                Footer(movie)
            }
        }
    }
}

@Composable
private fun Footer(movie: MovieUiModel) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text =
                if (movie.voteAverage.isNotBlank()) {
                    "★ ${movie.voteAverage}"
                } else {
                    "★ ${stringResource(Res.string.unrated)}"
                },
            style = MaterialTheme.typography.labelLarge,
        )
        Text(
            text =
                movie.releaseDate.ifBlank {
                    stringResource(Res.string.release_date_tba)
                },
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
private fun MovieOverview(overview: String) {
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
private fun MovieTitle(title: String) {
    Text(
        text = title,
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
            imageVector = Icons.Default.Movie,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private class MovieParameterProvider : PreviewParameterProvider<MovieUiModel> {
    override val values = sequenceOf(previewMovieUiModels[0], previewMovieUiModels[3])
}

@PreviewLightDark
@Composable
private fun MovieCardPreview(
    @PreviewParameter(MovieParameterProvider::class) movie: MovieUiModel,
) {
    SmoovieTheme { MovieCard(movie = movie, onClick = {}) }
}
