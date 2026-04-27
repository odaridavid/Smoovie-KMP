package dev.odaridavid.smoovie.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.odaridavid.smoovie.configuration.LoadConfigurationUseCase
import dev.odaridavid.smoovie.filter.FilterPreferencesStore
import dev.odaridavid.smoovie.filter.MovieFilterPreferences
import dev.odaridavid.smoovie.filter.MovieSortOption
import dev.odaridavid.smoovie.movies.domain.DiscoverMoviesUseCase
import dev.odaridavid.smoovie.movies.domain.GetGenresUseCase
import dev.odaridavid.smoovie.movies.domain.GetPopularMoviesUseCase
import dev.odaridavid.smoovie.movies.domain.GetTrendingMoviesUseCase
import dev.odaridavid.smoovie.movies.domain.MoviesPage
import dev.odaridavid.smoovie.movies.domain.SearchMoviesUseCase
import dev.odaridavid.smoovie.utils.toAppError
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class MoviesViewModel(
    private val getPopularMovies: GetPopularMoviesUseCase,
    private val getTrendingMovies: GetTrendingMoviesUseCase,
    private val searchMovies: SearchMoviesUseCase,
    private val discoverMovies: DiscoverMoviesUseCase,
    private val getGenres: GetGenresUseCase,
    private val loadConfiguration: LoadConfigurationUseCase,
    private val filterPreferencesStore: FilterPreferencesStore,
) : ViewModel() {
    private val _state = MutableStateFlow(MoviesScreenState())
    val state: StateFlow<MoviesScreenState> = _state.asStateFlow()

    private var currentPage = 1
    private var totalPages = 1

    init {
        viewModelScope.launch {
            val saved = filterPreferencesStore.getMovieFilter()
            _state.update { it.copy(filterPreferences = saved) }
            loadData()
        }
        observeSearchQuery()
    }

    fun onSearchQueryChanged(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    fun onFilterApplied(
        genreId: Int?,
        sortApiValue: String,
        minRating: Float,
    ) {
        val prefs =
            MovieFilterPreferences(
                selectedGenreId = genreId,
                sortBy = MovieSortOption.entries.find { it.apiValue == sortApiValue } ?: MovieSortOption.POPULARITY,
                minRating = minRating,
            )
        viewModelScope.launch {
            filterPreferencesStore.saveMovieFilter(prefs)
            _state.update { it.copy(filterPreferences = prefs, searchQuery = "") }
            loadMovies()
        }
    }

    fun loadMovies() {
        viewModelScope.launch {
            _state.update { it.copy(uiState = MoviesUiState.Loading) }
            try {
                _state.update { it.copy(uiState = processPage(fetchPage())) }
            } catch (e: Exception) {
                _state.update { it.copy(uiState = MoviesUiState.Error(e.toAppError())) }
            }
        }
    }

    fun retry() {
        loadData()
    }

    fun loadNextPage() {
        val currentUiState = _state.value.uiState as? MoviesUiState.Success ?: return
        if (currentUiState.isLoadingMore || !currentUiState.hasMorePages) return
        viewModelScope.launch {
            _state.update { it.copy(uiState = currentUiState.copy(isLoadingMore = true)) }
            try {
                val result = fetchPage(currentPage + 1)
                currentPage = result.page
                totalPages = result.totalPages
                val existingIds = currentUiState.movies.map { it.id }.toSet()
                val newMovies = result.movies.filter { it.id !in existingIds }
                _state.update {
                    it.copy(
                        uiState =
                            MoviesUiState.Success(
                                movies = currentUiState.movies + newMovies,
                                hasMorePages = currentPage < totalPages,
                            ),
                    )
                }
            } catch (_: Exception) {
                _state.update { it.copy(uiState = currentUiState.copy(isLoadingMore = false)) }
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        viewModelScope.launch {
            _state
                .map { it.searchQuery }
                .distinctUntilChanged()
                .drop(1)
                .debounce(300)
                .collectLatest {
                    _state.update { s -> s.copy(uiState = MoviesUiState.Loading) }
                    try {
                        _state.update { s -> s.copy(uiState = processPage(fetchPage())) }
                    } catch (e: Exception) {
                        _state.update { s -> s.copy(uiState = MoviesUiState.Error(e.toAppError())) }
                    }
                }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(uiState = MoviesUiState.Loading) }
            try {
                loadConfiguration()
                loadGenresList()
                supervisorScope {
                    val popularDeferred = async { fetchPage() }
                    val trendingDeferred = async { runCatching { getTrendingMovies() }.getOrElse { emptyList() } }
                    val uiState = processPage(popularDeferred.await())
                    val trending = trendingDeferred.await()
                    val featured = trending.ifEmpty { (uiState as? MoviesUiState.Success)?.movies.orEmpty() }
                    _state.update { it.copy(uiState = uiState, featuredMovies = featured) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(uiState = MoviesUiState.Error(e.toAppError())) }
            }
        }
    }

    private fun loadGenresList() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(genres = getGenres()) }
            } catch (_: Exception) {
            }
        }
    }

    private suspend fun fetchPage(page: Int = 1): MoviesPage {
        val query = _state.value.searchQuery
        val filter = _state.value.filterPreferences
        return when {
            query.isNotBlank() -> searchMovies(query, page)
            filter.isActive -> discoverMovies(filter, page)
            else -> getPopularMovies(page)
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
