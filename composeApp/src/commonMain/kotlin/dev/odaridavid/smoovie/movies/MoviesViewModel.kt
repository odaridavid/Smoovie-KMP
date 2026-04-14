package dev.odaridavid.smoovie.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.odaridavid.smoovie.configuration.ConfigurationRepository
import dev.odaridavid.smoovie.configuration.ConfigurationStore
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

class MoviesViewModel(
    private val moviesRepository: MoviesRepository,
    private val configurationRepository: ConfigurationRepository,
    private val configurationStore: ConfigurationStore,
    private val mapper: MovieUiMapper,
) : ViewModel() {
    private val _uiState = MutableStateFlow<MoviesUiState>(MoviesUiState.Loading)
    val uiState: StateFlow<MoviesUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadData()
        observeSearchQuery()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun loadMovies() {
        viewModelScope.launch {
            _uiState.value = MoviesUiState.Loading
            try {
                val query = _searchQuery.value
                val response = if (query.isBlank()) {
                    moviesRepository.getPopularMovies()
                } else {
                    moviesRepository.searchMovies(query)
                }
                _uiState.value = MoviesUiState.Success(mapper.toUiModels(response.results))
            } catch (e: Exception) {
                _uiState.value = MoviesUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery
                .drop(1)
                .debounce(300)
                .collectLatest { query ->
                    _uiState.value = MoviesUiState.Loading
                    try {
                        val response = if (query.isBlank()) {
                            moviesRepository.getPopularMovies()
                        } else {
                            moviesRepository.searchMovies(query)
                        }
                        _uiState.value = MoviesUiState.Success(mapper.toUiModels(response.results))
                    } catch (e: Exception) {
                        _uiState.value = MoviesUiState.Error(e.message ?: "Something went wrong")
                    }
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
