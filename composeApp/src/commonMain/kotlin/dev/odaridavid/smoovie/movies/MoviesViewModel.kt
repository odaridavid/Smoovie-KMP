package dev.odaridavid.smoovie.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.odaridavid.smoovie.configuration.LoadConfigurationUseCase
import dev.odaridavid.smoovie.movies.domain.GetPopularMoviesUseCase
import dev.odaridavid.smoovie.movies.domain.MoviesPage
import dev.odaridavid.smoovie.movies.domain.SearchMoviesUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

class MoviesViewModel(
    private val getPopularMovies: GetPopularMoviesUseCase,
    private val searchMovies: SearchMoviesUseCase,
    private val loadConfiguration: LoadConfigurationUseCase,
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
                val page = if (query.isBlank()) getPopularMovies() else searchMovies(query)
                _uiState.value = processPage(page)
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
                val result = if (query.isBlank()) getPopularMovies(nextPage) else searchMovies(query, nextPage)
                currentPage = result.page
                totalPages = result.totalPages
                val existingIds = currentState.movies.map { it.id }.toSet()
                val newMovies = result.movies.filter { it.id !in existingIds }
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
                        val page = if (query.isBlank()) getPopularMovies() else searchMovies(query)
                        _uiState.value = processPage(page)
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
                loadConfiguration()
                _uiState.value = processPage(getPopularMovies())
            } catch (e: Exception) {
                _uiState.value = MoviesUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }

    private fun processPage(result: MoviesPage): MoviesUiState {
        currentPage = result.page
        totalPages = result.totalPages
        return if (result.movies.isEmpty()) {
            MoviesUiState.Empty
        } else {
            MoviesUiState.Success(result.movies, hasMorePages = result.page < result.totalPages)
        }
    }
}
