package dev.odaridavid.smoovie.shows

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.odaridavid.smoovie.shows.domain.GetTvShowDetailUseCase
import dev.odaridavid.smoovie.utils.toAppError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TvShowDetailViewModel(
    private val tvShowId: Int,
    private val presentLabel: String,
    private val getTvShowDetail: GetTvShowDetailUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<TvShowDetailUiState>(TvShowDetailUiState.Loading)
    val uiState: StateFlow<TvShowDetailUiState> = _uiState.asStateFlow()

    init {
        loadTvShowDetail()
    }

    fun loadTvShowDetail() {
        viewModelScope.launch {
            _uiState.value = TvShowDetailUiState.Loading
            try {
                _uiState.value = TvShowDetailUiState.Success(getTvShowDetail(tvShowId, presentLabel))
            } catch (e: Exception) {
                _uiState.value = TvShowDetailUiState.Error(e.toAppError())
            }
        }
    }
}
