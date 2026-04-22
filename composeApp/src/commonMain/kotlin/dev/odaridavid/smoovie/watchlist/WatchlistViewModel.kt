package dev.odaridavid.smoovie.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.odaridavid.smoovie.movies.MovieUiModel
import dev.odaridavid.smoovie.watchlist.domain.ObserveWatchlistUseCase
import dev.odaridavid.smoovie.watchlist.domain.RemoveFromWatchlistUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WatchlistViewModel(
    observeWatchlist: ObserveWatchlistUseCase,
    private val removeFromWatchlist: RemoveFromWatchlistUseCase,
) : ViewModel() {
    val state: StateFlow<WatchlistUiState> =
        observeWatchlist()
            .map { movies ->
                if (movies.isEmpty()) {
                    WatchlistUiState.Empty
                } else {
                    WatchlistUiState.Loaded(movies)
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STATE_TIMEOUT_MS),
                initialValue = WatchlistUiState.Loading,
            )

    fun remove(movie: MovieUiModel) {
        viewModelScope.launch { removeFromWatchlist(movie) }
    }

    private companion object {
        const val STATE_TIMEOUT_MS = 5_000L
    }
}
