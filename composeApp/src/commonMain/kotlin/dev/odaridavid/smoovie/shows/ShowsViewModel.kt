package dev.odaridavid.smoovie.shows

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.odaridavid.smoovie.configuration.LoadConfigurationUseCase
import dev.odaridavid.smoovie.filter.FilterPreferencesStore
import dev.odaridavid.smoovie.filter.TvFilterPreferences
import dev.odaridavid.smoovie.filter.TvSortOption
import dev.odaridavid.smoovie.shows.domain.DiscoverTvShowsUseCase
import dev.odaridavid.smoovie.shows.domain.GetPopularTvShowsUseCase
import dev.odaridavid.smoovie.shows.domain.GetTvGenresUseCase
import dev.odaridavid.smoovie.shows.domain.SearchTvShowsUseCase
import dev.odaridavid.smoovie.shows.domain.TvShowsPage
import dev.odaridavid.smoovie.utils.toAppError
import kotlinx.coroutines.FlowPreview
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

class ShowsViewModel(
    private val getPopularTvShows: GetPopularTvShowsUseCase,
    private val searchTvShows: SearchTvShowsUseCase,
    private val discoverTvShows: DiscoverTvShowsUseCase,
    private val getTvGenres: GetTvGenresUseCase,
    private val loadConfiguration: LoadConfigurationUseCase,
    private val filterPreferencesStore: FilterPreferencesStore,
) : ViewModel() {
    private val _state = MutableStateFlow(ShowsScreenState())
    val state: StateFlow<ShowsScreenState> = _state.asStateFlow()

    private var currentPage = 1
    private var totalPages = 1

    init {
        viewModelScope.launch {
            val saved = filterPreferencesStore.getTvFilter()
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
            TvFilterPreferences(
                selectedGenreId = genreId,
                sortBy = TvSortOption.entries.find { it.apiValue == sortApiValue } ?: TvSortOption.POPULARITY,
                minRating = minRating,
            )
        viewModelScope.launch {
            filterPreferencesStore.saveTvFilter(prefs)
            _state.update { it.copy(filterPreferences = prefs, searchQuery = "") }
            loadShows()
        }
    }

    fun loadShows() {
        viewModelScope.launch {
            _state.update { it.copy(uiState = ShowsUiState.Loading) }
            try {
                _state.update { it.copy(uiState = processPage(fetchPage())) }
            } catch (e: Exception) {
                _state.update { it.copy(uiState = ShowsUiState.Error(e.toAppError())) }
            }
        }
    }

    fun retry() {
        loadData()
    }

    fun loadNextPage() {
        val currentUiState = _state.value.uiState as? ShowsUiState.Success ?: return
        if (currentUiState.isLoadingMore || !currentUiState.hasMorePages) return
        viewModelScope.launch {
            _state.update { it.copy(uiState = currentUiState.copy(isLoadingMore = true)) }
            try {
                val result = fetchPage(currentPage + 1)
                currentPage = result.page
                totalPages = result.totalPages
                val existingIds = currentUiState.tvShows.map { it.id }.toSet()
                val newShows = result.tvShows.filter { it.id !in existingIds }
                _state.update {
                    it.copy(
                        uiState =
                            ShowsUiState.Success(
                                tvShows = currentUiState.tvShows + newShows,
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
                    _state.update { s -> s.copy(uiState = ShowsUiState.Loading) }
                    try {
                        _state.update { s -> s.copy(uiState = processPage(fetchPage())) }
                    } catch (e: Exception) {
                        _state.update { s -> s.copy(uiState = ShowsUiState.Error(e.toAppError())) }
                    }
                }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(uiState = ShowsUiState.Loading) }
            try {
                loadConfiguration()
                loadGenresList()
                val uiState = processPage(fetchPage())
                val featured = (uiState as? ShowsUiState.Success)?.tvShows ?: emptyList()
                _state.update { it.copy(uiState = uiState, featuredTvShows = featured) }
            } catch (e: Exception) {
                _state.update { it.copy(uiState = ShowsUiState.Error(e.toAppError())) }
            }
        }
    }

    private fun loadGenresList() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(genres = getTvGenres()) }
            } catch (_: Exception) {
            }
        }
    }

    private suspend fun fetchPage(page: Int = 1): TvShowsPage {
        val query = _state.value.searchQuery
        val filter = _state.value.filterPreferences
        return when {
            query.isNotBlank() -> searchTvShows(query, page)
            filter.isActive -> discoverTvShows(filter, page)
            else -> getPopularTvShows(page)
        }
    }

    private fun processPage(result: TvShowsPage): ShowsUiState {
        currentPage = result.page
        totalPages = result.totalPages
        return if (result.tvShows.isEmpty()) {
            ShowsUiState.Empty
        } else {
            ShowsUiState.Success(result.tvShows, hasMorePages = result.page < result.totalPages)
        }
    }
}
