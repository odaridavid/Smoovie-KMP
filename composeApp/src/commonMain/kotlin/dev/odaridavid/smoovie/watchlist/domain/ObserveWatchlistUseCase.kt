package dev.odaridavid.smoovie.watchlist.domain

import dev.odaridavid.smoovie.movies.MovieUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveWatchlistUseCase(
    private val repository: WatchlistRepository,
) {
    operator fun invoke(): Flow<List<MovieUiModel>> = repository.observeAll().map { entries -> entries.map { it.toMovieUiModel() } }
}
