package dev.odaridavid.smoovie.watchlist.domain

import dev.odaridavid.smoovie.movies.MovieUiModel
import dev.odaridavid.smoovie.shows.TvShowUiModel

class ToggleWatchlistUseCase(
    private val repository: WatchlistRepository,
) {
    suspend operator fun invoke(movie: MovieUiModel) {
        repository.toggle(movie.toWatchlistEntry())
    }

    suspend operator fun invoke(tvShow: TvShowUiModel) {
        repository.toggle(tvShow.toWatchlistEntry())
    }
}
