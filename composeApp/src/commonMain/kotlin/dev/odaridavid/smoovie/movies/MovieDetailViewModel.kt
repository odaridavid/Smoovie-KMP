package dev.odaridavid.smoovie.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.odaridavid.smoovie.configuration.BackdropSize
import dev.odaridavid.smoovie.configuration.ConfigurationStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MovieDetailViewModel(
    private val movieId: Int,
    private val moviesRepository: MoviesRepository,
    private val configurationStore: ConfigurationStore,
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
                val detail = moviesRepository.getMovieDetail(movieId)
                _uiState.value =
                    MovieDetailUiState.Success(
                        detail.toDetailUiModel(
                            backdropUrl =
                                configurationStore.backdropUrl(
                                    detail.backdropPath,
                                    BackdropSize.LARGE,
                                ),
                            posterUrl = configurationStore.posterUrl(detail.posterPath),
                            profileUrlResolver = { configurationStore.profileUrl(it) },
                        ),
                    )
            } catch (e: Exception) {
                _uiState.value = MovieDetailUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }
}
