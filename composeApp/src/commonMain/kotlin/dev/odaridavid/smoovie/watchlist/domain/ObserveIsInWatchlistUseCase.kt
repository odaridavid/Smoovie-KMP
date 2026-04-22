package dev.odaridavid.smoovie.watchlist.domain

import kotlinx.coroutines.flow.Flow

class ObserveIsInWatchlistUseCase(
    private val repository: WatchlistRepository,
) {
    operator fun invoke(movieId: Int): Flow<Boolean> = repository.observeContains(movieId)
}
