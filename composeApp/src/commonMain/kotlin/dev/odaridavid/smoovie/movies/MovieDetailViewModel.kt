package dev.odaridavid.smoovie.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.odaridavid.smoovie.movies.domain.GetMovieDetailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MovieDetailViewModel(
    private val movieId: Int,
    private val getMovieDetail: GetMovieDetailUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<MovieDetailUiState>(MovieDetailUiState.Loading)
    val uiState: StateFlow<MovieDetailUiState> = _uiState.asStateFlow()

    init {
        loadMovieDetail()
    }

    fun loadMovieDetail() {
        viewModelScope.launch {
            _uiState.value = MovieDetailUiState.Loading
            try {
                _uiState.value = MovieDetailUiState.Success(getMovieDetail(movieId))
            } catch (e: Exception) {
                _uiState.value = MovieDetailUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }
}
