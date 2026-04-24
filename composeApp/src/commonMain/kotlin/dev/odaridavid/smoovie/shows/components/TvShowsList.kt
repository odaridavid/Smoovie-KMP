package dev.odaridavid.smoovie.shows.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.odaridavid.smoovie.shows.TvShowUiModel

internal fun LazyListScope.tvShowItems(
    tvShows: List<TvShowUiModel>,
    animatedIds: MutableSet<Int>,
    isLoadingMore: Boolean,
    onTvShowClick: (TvShowUiModel) -> Unit,
) {
    itemsIndexed(tvShows, key = { _, show -> show.id }) { index, show ->
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            AnimatedTvShowCard(
                tvShow = show,
                index = index,
                skipAnimation = show.id in animatedIds,
                onAnimationEnd = { animatedIds.add(show.id) },
                onClick = { onTvShowClick(show) },
            )
        }
    }
    if (isLoadingMore) {
        item(key = "loading_more") {
            Box(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
