package dev.odaridavid.smoovie.watchlist.domain

import dev.odaridavid.smoovie.movies.MovieUiModel

class ToggleWatchlistUseCase(
    private val repository: WatchlistRepository,
) {
    suspend operator fun invoke(movie: MovieUiModel) {
        repository.toggle(movie.toWatchlistEntry())
    }
}
