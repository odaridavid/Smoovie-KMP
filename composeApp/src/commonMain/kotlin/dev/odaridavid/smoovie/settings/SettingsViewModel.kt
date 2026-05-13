package dev.odaridavid.smoovie.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.odaridavid.smoovie.observability.setCrashReportingEnabled
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
            SettingsUiState(
                selectedRegion = resolveRegion(settingsPreferencesStore.regionCode.value),
                crashReportingEnabled = settingsPreferencesStore.crashReportingEnabled.value,
            ),
        )
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            settingsPreferencesStore.regionCode.collect { code ->
                _state.update { it.copy(selectedRegion = resolveRegion(code)) }
            }
        }
        viewModelScope.launch {
            settingsPreferencesStore.crashReportingEnabled.collect { enabled ->
                _state.update { it.copy(crashReportingEnabled = enabled) }
            }
        }
    }

    fun onRegionSelected(region: Region) {
        viewModelScope.launch {
            settingsPreferencesStore.setRegionCode(region.code)
        }
    }

    fun onCrashReportingToggled(enabled: Boolean) {
        viewModelScope.launch {
            settingsPreferencesStore.setCrashReportingEnabled(enabled)
            setCrashReportingEnabled(enabled)
        }
    }
}
