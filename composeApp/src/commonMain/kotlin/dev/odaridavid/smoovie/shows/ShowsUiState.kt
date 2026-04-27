package dev.odaridavid.smoovie.shows

import dev.odaridavid.smoovie.filter.TvFilterPreferences
import dev.odaridavid.smoovie.utils.AppError

data class ShowsScreenState(
    val uiState: ShowsUiState = ShowsUiState.Loading,
    val searchQuery: String = "",
    val genres: List<TvGenreUiModel> = emptyList(),
    val filterPreferences: TvFilterPreferences = TvFilterPreferences(),
    val featuredTvShows: List<TvShowUiModel> = emptyList(),
)

sealed interface ShowsUiState {
    data object Loading : ShowsUiState

    data class Success(
        val tvShows: List<TvShowUiModel>,
        val isLoadingMore: Boolean = false,
        val hasMorePages: Boolean = false,
    ) : ShowsUiState

    data object Empty : ShowsUiState

    data class Error(
        val error: AppError,
    ) : ShowsUiState
}
