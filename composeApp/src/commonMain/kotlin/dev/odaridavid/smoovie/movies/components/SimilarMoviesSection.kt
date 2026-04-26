package dev.odaridavid.smoovie.movies.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Movie
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
import dev.odaridavid.smoovie.movies.MovieUiModel
import dev.odaridavid.smoovie.theme.SmoovieTheme
import org.jetbrains.compose.resources.stringResource
import dev.odaridavid.smoovie.utils.previewMovieUiModels
import smoovie.composeapp.generated.resources.Res
import smoovie.composeapp.generated.resources.similar_section_title

@Composable
internal fun SimilarMoviesSection(
    movies: List<MovieUiModel>,
    onMovieClick: (MovieUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(Res.string.similar_section_title),
            style = MaterialTheme.typography.titleMedium,
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(movies, key = { it.id }) { movie ->
                SimilarMovieItem(movie = movie, onClick = { onMovieClick(movie) })
            }
        }
    }
}

@Composable
private fun SimilarMovieItem(
    movie: MovieUiModel,
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
            contentAlignment = Alignment.Center,
        ) {
            SubcomposeAsyncImage(
                model = movie.posterUrl,
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                loading = { PosterPlaceholder() },
                error = { PosterPlaceholder() },
                modifier = Modifier.fillMaxSize(),
            )
        }
        Text(
            text = movie.title,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        if (movie.voteAverage.isNotBlank()) {
            Text(
                text = "★ ${movie.voteAverage}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun PosterPlaceholder() {
    Icon(
        imageVector = Icons.Default.Movie,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

private val POSTER_WIDTH = 120.dp
private const val POSTER_ASPECT_RATIO = 2f / 3f

// region Previews

@PreviewLightDark
@Composable
private fun SimilarMoviesSectionPreview() {
    SmoovieTheme {
        SimilarMoviesSection(
            movies = previewMovieUiModels.take(5),
            onMovieClick = {},
        )
    }
}

// endregion
