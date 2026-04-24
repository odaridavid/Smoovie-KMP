package dev.odaridavid.smoovie.shows

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.odaridavid.smoovie.configuration.LoadConfigurationUseCase
import dev.odaridavid.smoovie.shows.domain.GetPopularTvShowsUseCase
import dev.odaridavid.smoovie.shows.domain.GetTvGenresUseCase
import dev.odaridavid.smoovie.shows.domain.GetTvShowsByGenreUseCase
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
    private val getTvShowsByGenre: GetTvShowsByGenreUseCase,
    private val getTvGenres: GetTvGenresUseCase,
    private val loadConfiguration: LoadConfigurationUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(ShowsScreenState())
    val state: StateFlow<ShowsScreenState> = _state.asStateFlow()

    private var currentPage = 1
    private var totalPages = 1

    init {
        loadData()
        observeSearchQuery()
    }

    fun onSearchQueryChanged(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    fun onGenreSelected(genre: TvGenreUiModel?) {
        if (_state.value.selectedGenre == genre) return
        _state.update { it.copy(selectedGenre = genre, searchQuery = "") }
        loadShows()
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
                _state.update { it.copy(uiState = processPage(fetchPage())) }
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
        val genreId = _state.value.selectedGenre?.id
        return when {
            query.isNotBlank() -> searchTvShows(query, page)
            genreId != null -> getTvShowsByGenre(genreId, page)
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
