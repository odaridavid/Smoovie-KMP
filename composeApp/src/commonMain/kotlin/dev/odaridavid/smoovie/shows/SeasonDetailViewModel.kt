package dev.odaridavid.smoovie.shows

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.odaridavid.smoovie.shows.domain.GetSeasonDetailUseCase
import dev.odaridavid.smoovie.utils.toAppError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SeasonDetailViewModel(
    private val tvShowId: Int,
    private val seasonNumber: Int,
    private val getSeasonDetail: GetSeasonDetailUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<SeasonDetailUiState>(SeasonDetailUiState.Loading)
    val uiState: StateFlow<SeasonDetailUiState> = _uiState.asStateFlow()

    init {
        loadSeasonDetail()
    }

    fun loadSeasonDetail() {
        viewModelScope.launch {
            _uiState.value = SeasonDetailUiState.Loading
            try {
                _uiState.value = SeasonDetailUiState.Success(getSeasonDetail(tvShowId, seasonNumber))
            } catch (e: Exception) {
                _uiState.value = SeasonDetailUiState.Error(e.toAppError())
            }
        }
    }
}
