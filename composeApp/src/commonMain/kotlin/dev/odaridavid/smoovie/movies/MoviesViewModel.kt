package dev.odaridavid.smoovie.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.odaridavid.smoovie.configuration.ConfigurationRepository
import dev.odaridavid.smoovie.configuration.ConfigurationStore
import dev.odaridavid.smoovie.movies.data.MoviesResponse
import dev.odaridavid.smoovie.movies.domain.MoviesRepository
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

    private var currentPage = 1
    private var totalPages = 1

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
                val response =
                    if (query.isBlank()) {
                        moviesRepository.getPopularMovies()
                    } else {
                        moviesRepository.searchMovies(query)
                    }
                _uiState.value = processResponse(response)
            } catch (e: Exception) {
                _uiState.value = MoviesUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }

    fun loadNextPage() {
        val currentState = _uiState.value as? MoviesUiState.Success ?: return
        if (currentState.isLoadingMore || !currentState.hasMorePages) return
        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoadingMore = true)
            try {
                val nextPage = currentPage + 1
                val query = _searchQuery.value
                val response =
                    if (query.isBlank()) {
                        moviesRepository.getPopularMovies(nextPage)
                    } else {
                        moviesRepository.searchMovies(query, nextPage)
                    }
                currentPage = response.page
                totalPages = response.totalPages
                val existingIds = currentState.movies.map { it.id }.toSet()
                val newMovies = mapper.toUiModels(response.results).filter { it.id !in existingIds }
                _uiState.value =
                    MoviesUiState.Success(
                        movies = currentState.movies + newMovies,
                        hasMorePages = currentPage < totalPages,
                    )
            } catch (_: Exception) {
                _uiState.value = currentState.copy(isLoadingMore = false)
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
                        val response =
                            if (query.isBlank()) {
                                moviesRepository.getPopularMovies()
                            } else {
                                moviesRepository.searchMovies(query)
                            }
                        _uiState.value = processResponse(response)
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
                _uiState.value = processResponse(response)
            } catch (e: Exception) {
                _uiState.value = MoviesUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }

    private fun processResponse(response: MoviesResponse): MoviesUiState {
        currentPage = response.page
        totalPages = response.totalPages
        val uiModels = mapper.toUiModels(response.results)
        return if (uiModels.isEmpty()) {
            MoviesUiState.Empty
        } else {
            MoviesUiState.Success(uiModels, hasMorePages = response.page < response.totalPages)
        }
    }
}
