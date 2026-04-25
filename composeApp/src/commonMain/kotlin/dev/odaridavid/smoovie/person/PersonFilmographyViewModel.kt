package dev.odaridavid.smoovie.person

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.odaridavid.smoovie.person.domain.GetPersonDetailUseCase
import dev.odaridavid.smoovie.utils.toAppError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PersonFilmographyViewModel(
    private val personId: Int,
    private val getPersonDetail: GetPersonDetailUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<PersonFilmographyUiState>(PersonFilmographyUiState.Loading)
    val uiState: StateFlow<PersonFilmographyUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = PersonFilmographyUiState.Loading
            try {
                _uiState.value = PersonFilmographyUiState.Success(getPersonDetail(personId))
            } catch (e: Exception) {
                _uiState.value = PersonFilmographyUiState.Error(e.toAppError())
            }
        }
    }
}
