package dev.odaridavid.smoovie.person

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.odaridavid.smoovie.person.domain.GetPersonDetailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PersonDetailViewModel(
    private val personId: Int,
    private val getPersonDetail: GetPersonDetailUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<PersonDetailUiState>(PersonDetailUiState.Loading)
    val uiState: StateFlow<PersonDetailUiState> = _uiState.asStateFlow()

    init {
        loadPersonDetail()
    }

    fun loadPersonDetail() {
        viewModelScope.launch {
            _uiState.value = PersonDetailUiState.Loading
            try {
                _uiState.value = PersonDetailUiState.Success(getPersonDetail(personId))
            } catch (e: Exception) {
                _uiState.value = PersonDetailUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }
}
