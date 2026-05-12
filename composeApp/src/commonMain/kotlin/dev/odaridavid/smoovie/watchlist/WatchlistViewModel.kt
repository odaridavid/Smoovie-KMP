package dev.odaridavid.smoovie.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.odaridavid.smoovie.utils.AppReviewRequester
import dev.odaridavid.smoovie.watchlist.domain.MediaType
import dev.odaridavid.smoovie.watchlist.domain.ObserveWatchlistUseCase
import dev.odaridavid.smoovie.watchlist.domain.RemoveFromWatchlistUseCase
import dev.odaridavid.smoovie.watchlist.domain.WatchlistEntry
import dev.odaridavid.smoovie.watchlist.domain.toMovieUiModel
import dev.odaridavid.smoovie.watchlist.domain.toTvShowUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WatchlistViewModel(
    private val observeWatchlist: ObserveWatchlistUseCase,
    private val removeFromWatchlist: RemoveFromWatchlistUseCase,
    private val appReviewRequester: AppReviewRequester,
) : ViewModel() {
    private val filter = MutableStateFlow(WatchlistFilter.ALL)

    init {
        viewModelScope.launch {
            observeWatchlist()
                .map { it.size }
                .distinctUntilChanged()
                .collect { count ->
                    if (count == REVIEW_PROMPT_THRESHOLD) appReviewRequester.requestReview()
                }
        }
    }

    val state: StateFlow<WatchlistUiState> =
        combine(observeWatchlist(), filter) { entries, currentFilter ->
            when {
                entries.isEmpty() -> WatchlistUiState.Empty
                else ->
                    WatchlistUiState.Loaded(
                        filter = currentFilter,
                        items = entries.applyFilter(currentFilter).map { it.toUiModel() },
                    )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STATE_TIMEOUT_MS),
            initialValue = WatchlistUiState.Loading,
        )

    fun setFilter(newFilter: WatchlistFilter) {
        filter.value = newFilter
    }

    fun remove(item: WatchlistItemUiModel) {
        val (id, mediaType) =
            when (item) {
                is WatchlistItemUiModel.Movie -> item.movie.id to MediaType.MOVIE
                is WatchlistItemUiModel.TvShow -> item.tvShow.id to MediaType.TV
            }
        viewModelScope.launch { removeFromWatchlist(id, mediaType) }
    }

    private fun List<WatchlistEntry>.applyFilter(filter: WatchlistFilter): List<WatchlistEntry> =
        when (filter) {
            WatchlistFilter.ALL -> this
            WatchlistFilter.MOVIES -> filter { it.mediaType == MediaType.MOVIE }
            WatchlistFilter.TV_SHOWS -> filter { it.mediaType == MediaType.TV }
        }

    private fun WatchlistEntry.toUiModel(): WatchlistItemUiModel =
        when (mediaType) {
            MediaType.MOVIE -> WatchlistItemUiModel.Movie(toMovieUiModel())
            MediaType.TV -> WatchlistItemUiModel.TvShow(toTvShowUiModel())
        }

    private companion object {
        const val STATE_TIMEOUT_MS = 5_000L
        const val REVIEW_PROMPT_THRESHOLD = 3
    }
}
