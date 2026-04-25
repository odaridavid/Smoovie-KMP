package dev.odaridavid.smoovie.watchlist.domain

import kotlinx.coroutines.flow.Flow

class ObserveIsInWatchlistUseCase(
    private val repository: WatchlistRepository,
) {
    operator fun invoke(
        id: Int,
        mediaType: MediaType,
    ): Flow<Boolean> = repository.observeContains(id, mediaType)
}
