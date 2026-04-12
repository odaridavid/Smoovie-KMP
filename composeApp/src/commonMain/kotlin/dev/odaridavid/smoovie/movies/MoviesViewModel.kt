package dev.odaridavid.smoovie.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.odaridavid.smoovie.configuration.ConfigurationRepository
import dev.odaridavid.smoovie.configuration.ConfigurationStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class MoviesViewModel(
    private val moviesRepository: MoviesRepository,
    private val configurationRepository: ConfigurationRepository,
    private val configurationStore: ConfigurationStore,
    private val mapper: MovieUiMapper,
) : ViewModel() {
    private val _uiState = MutableStateFlow<MoviesUiState>(MoviesUiState.Loading)
    val uiState: StateFlow<MoviesUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadMovies() {
        viewModelScope.launch {
            _uiState.value = MoviesUiState.Loading
            try {
                val response = moviesRepository.getPopularMovies()
                _uiState.value = MoviesUiState.Success(mapper.toUiModels(response.results))
            } catch (e: Exception) {
                _uiState.value = MoviesUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = MoviesUiState.Loading
            try {
                val config = configurationRepository.getImagesConfiguration()
                configurationStore.save(config)
                val response = moviesRepository.getPopularMovies()
                _uiState.value = MoviesUiState.Success(mapper.toUiModels(response.results))
            } catch (e: Exception) {
                _uiState.value = MoviesUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }
}
