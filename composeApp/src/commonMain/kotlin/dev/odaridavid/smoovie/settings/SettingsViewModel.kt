package dev.odaridavid.smoovie.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsPreferencesStore: SettingsPreferencesStore,
) : ViewModel() {
    private val _state =
        MutableStateFlow(
            SettingsUiState(selectedRegion = resolveRegion(settingsPreferencesStore.regionCode.value)),
        )
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            settingsPreferencesStore.regionCode.collect { code ->
                _state.update { it.copy(selectedRegion = resolveRegion(code)) }
            }
        }
    }

    fun onRegionSelected(region: Region) {
        viewModelScope.launch {
            settingsPreferencesStore.setRegionCode(region.code)
        }
    }
}
