package dev.odaridavid.smoovie.watchlist.domain

class RemoveFromWatchlistUseCase(
    private val repository: WatchlistRepository,
) {
    suspend operator fun invoke(
        id: Int,
        mediaType: MediaType,
    ) {
        repository.remove(id, mediaType)
    }
}
