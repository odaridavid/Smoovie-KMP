package dev.odaridavid.smoovie.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import dev.odaridavid.smoovie.filter.FilterGenreOption
import dev.odaridavid.smoovie.filter.SortEntry
import dev.odaridavid.smoovie.theme.SmoovieTheme
import org.jetbrains.compose.resources.stringResource
import smoovie.composeapp.generated.resources.Res
import smoovie.composeapp.generated.resources.filter_apply
import smoovie.composeapp.generated.resources.filter_genre_section
import smoovie.composeapp.generated.resources.filter_min_rating_section
import smoovie.composeapp.generated.resources.filter_reset
import smoovie.composeapp.generated.resources.filter_sheet_title
import smoovie.composeapp.generated.resources.filter_sort_section
import smoovie.composeapp.generated.resources.genre_filter_all
import kotlin.math.roundToInt

private const val RATING_STEP_COUNT = 20

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FilterSheet(
    genres: List<FilterGenreOption>,
    sortEntries: List<SortEntry>,
    selectedGenreId: Int?,
    selectedSortApiValue: String,
    minRating: Float,
    onApply: (genreId: Int?, sortEntry: SortEntry, minRating: Float) -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(),
) {
    var draftGenreId by remember { mutableStateOf(selectedGenreId) }
    var draftSortEntry by remember { mutableStateOf(sortEntries.find { it.apiValue == selectedSortApiValue } ?: sortEntries.first()) }
    var draftRating by remember { mutableFloatStateOf(minRating) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
        ) {
            Text(
                text = stringResource(Res.string.filter_sheet_title),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(Res.string.filter_genre_section),
                style = MaterialTheme.typography.labelLarge,
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                contentPadding = PaddingValues(end = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    FilterChip(
                        selected = draftGenreId == null,
                        onClick = { draftGenreId = null },
                        label = { Text(stringResource(Res.string.genre_filter_all)) },
                    )
                }
                items(genres, key = { it.id }) { genre ->
                    FilterChip(
                        selected = draftGenreId == genre.id,
                        onClick = { draftGenreId = if (draftGenreId == genre.id) null else genre.id },
                        label = { Text(genre.name) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(Res.string.filter_sort_section),
                style = MaterialTheme.typography.labelLarge,
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                contentPadding = PaddingValues(end = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(sortEntries, key = { it.apiValue }) { entry ->
                    FilterChip(
                        selected = draftSortEntry.apiValue == entry.apiValue,
                        onClick = { draftSortEntry = entry },
                        label = { Text(entry.label) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.filter_min_rating_section),
                    style = MaterialTheme.typography.labelLarge,
                )
                val displayRating = (draftRating * 2).roundToInt() / 2f
                Text(
                    text = if (displayRating > 0f) "★ $displayRating" else "Any",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Slider(
                value = draftRating,
                onValueChange = { draftRating = it },
                valueRange = 0f..10f,
                steps = RATING_STEP_COUNT - 1,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = {
                        draftGenreId = null
                        draftSortEntry = sortEntries.first()
                        draftRating = 0f
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(),
                ) {
                    Text(stringResource(Res.string.filter_reset))
                }
                TextButton(
                    onClick = { onApply(draftGenreId, draftSortEntry, draftRating) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                ) {
                    Text(
                        text = stringResource(Res.string.filter_apply),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}

// region Previews

private val previewGenres =
    listOf(
        FilterGenreOption(28, "Action"),
        FilterGenreOption(35, "Comedy"),
        FilterGenreOption(18, "Drama"),
        FilterGenreOption(10751, "Family"),
    )

private val previewSortEntries =
    listOf(
        SortEntry("Popularity", "popularity.desc"),
        SortEntry("Rating", "vote_average.desc"),
        SortEntry("Newest", "primary_release_date.desc"),
        SortEntry("Revenue", "revenue.desc"),
    )

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun FilterSheetDefaultPreview() {
    SmoovieTheme {
        FilterSheet(
            genres = previewGenres,
            sortEntries = previewSortEntries,
            selectedGenreId = null,
            selectedSortApiValue = previewSortEntries.first().apiValue,
            minRating = 0f,
            onApply = { _, _, _ -> },
            onDismiss = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun FilterSheetActivePreview() {
    SmoovieTheme {
        FilterSheet(
            genres = previewGenres,
            sortEntries = previewSortEntries,
            selectedGenreId = 28,
            selectedSortApiValue = previewSortEntries[1].apiValue,
            minRating = 7f,
            onApply = { _, _, _ -> },
            onDismiss = {},
        )
    }
}

// endregion
