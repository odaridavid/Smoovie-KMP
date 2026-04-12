package dev.odaridavid.smoovie.movies

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.odaridavid.smoovie.theme.SmoovieTheme
import previewMovieUiModels

@Composable
internal fun MovieCard(movie: MovieUiModel) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(96.dp)
                    .fillMaxHeight(),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = movie.overview,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "★ ${movie.voteAverage}",
                        style = MaterialTheme.typography.labelLarge,
                    )
                    Text(
                        text = movie.releaseDate,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun MovieCardPreview() {
    SmoovieTheme {
        MovieCard(movie = previewMovieUiModels.first())
    }
}