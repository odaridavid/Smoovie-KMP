package dev.odaridavid.smoovie.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.odaridavid.smoovie.watchlist.domain.ObserveWatchlistUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class WatchlistViewModel(
    observeWatchlist: ObserveWatchlistUseCase,
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

    private companion object {
        const val STATE_TIMEOUT_MS = 5_000L
    }
}
