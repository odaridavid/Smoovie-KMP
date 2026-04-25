package dev.odaridavid.smoovie.watchlist.domain

import kotlinx.coroutines.flow.Flow

class ObserveWatchlistUseCase(
    private val repository: WatchlistRepository,
) {
    operator fun invoke(): Flow<List<WatchlistEntry>> = repository.observeAll()
}
