package dev.odaridavid.smoovie.shows.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import dev.odaridavid.smoovie.shows.TvGenreUiModel
import org.jetbrains.compose.resources.stringResource
import smoovie.composeapp.generated.resources.Res
import smoovie.composeapp.generated.resources.genre_filter_all

@Composable
internal fun TvGenreChips(
    genres: List<TvGenreUiModel>,
    selectedGenre: TvGenreUiModel?,
    onGenreSelected: (TvGenreUiModel?) -> Unit,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            FilterChip(
                selected = selectedGenre == null,
                onClick = { onGenreSelected(null) },
                label = { Text(stringResource(Res.string.genre_filter_all)) },
            )
        }
        items(genres, key = { it.id }) { genre ->
            FilterChip(
                selected = selectedGenre?.id == genre.id,
                onClick = { onGenreSelected(if (selectedGenre?.id == genre.id) null else genre) },
                label = { Text(genre.name) },
            )
        }
    }
}
