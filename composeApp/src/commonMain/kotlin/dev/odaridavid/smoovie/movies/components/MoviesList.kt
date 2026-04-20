package dev.odaridavid.smoovie.movies.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.odaridavid.smoovie.movies.MovieUiModel

internal fun LazyListScope.movieItems(
    movies: List<MovieUiModel>,
    animatedIds: MutableSet<Int>,
    isLoadingMore: Boolean,
    onMovieClick: (MovieUiModel) -> Unit,
) {
    itemsIndexed(movies, key = { _, movie -> movie.id }) { index, movie ->
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            AnimatedMovieCard(
                movie = movie,
                index = index,
                skipAnimation = movie.id in animatedIds,
                onAnimationEnd = { animatedIds.add(movie.id) },
                onClick = { onMovieClick(movie) },
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
