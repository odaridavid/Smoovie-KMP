package dev.odaridavid.smoovie.movies

import dev.odaridavid.smoovie.filter.MovieFilterPreferences
import dev.odaridavid.smoovie.utils.AppError

data class MoviesScreenState(
    val uiState: MoviesUiState = MoviesUiState.Loading,
    val searchQuery: String = "",
    val genres: List<GenreUiModel> = emptyList(),
    val filterPreferences: MovieFilterPreferences = MovieFilterPreferences(),
    val featuredMovies: List<MovieUiModel> = emptyList(),
)

sealed interface MoviesUiState {
    data object Loading : MoviesUiState

    data class Success(
        val movies: List<MovieUiModel>,
        val isLoadingMore: Boolean = false,
        val hasMorePages: Boolean = false,
    ) : MoviesUiState

    data object Empty : MoviesUiState

    data class Error(
        val error: AppError,
    ) : MoviesUiState
}
