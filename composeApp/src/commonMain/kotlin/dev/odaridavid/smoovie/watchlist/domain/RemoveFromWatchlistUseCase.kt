package dev.odaridavid.smoovie.watchlist.domain

import dev.odaridavid.smoovie.movies.MovieUiModel

class RemoveFromWatchlistUseCase(
    private val repository: WatchlistRepository,
) {
    suspend operator fun invoke(movie: MovieUiModel) {
        repository.remove(movie.id)
    }
}
