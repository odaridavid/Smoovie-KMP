package dev.odaridavid.smoovie.ui.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.odaridavid.smoovie.data.TmdbApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MoviesViewModel(
    private val api: TmdbApi,
) : ViewModel() {
    private val _uiState = MutableStateFlow<MoviesUiState>(MoviesUiState.Loading)
    val uiState: StateFlow<MoviesUiState> = _uiState.asStateFlow()

    init {
        loadMovies()
    }

    fun loadMovies() {
        viewModelScope.launch {
            _uiState.value = MoviesUiState.Loading
            try {
                val response = api.getPopularMovies()
                _uiState.value = MoviesUiState.Success(response.results)
            } catch (e: Exception) {
                _uiState.value = MoviesUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }
}
