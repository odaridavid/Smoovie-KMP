package dev.odaridavid.smoovie.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.odaridavid.smoovie.observability.setCrashReportingEnabled
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CrashReportingConsentViewModel(
    private val settingsPreferencesStore: SettingsPreferencesStore,
) : ViewModel() {
    val isVisible: StateFlow<Boolean> =
        settingsPreferencesStore.hasSeenCrashReportingPrompt
            .map { seen -> !seen }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = !settingsPreferencesStore.hasSeenCrashReportingPrompt.value,
            )

    fun onEnable() {
        viewModelScope.launch {
            settingsPreferencesStore.setCrashReportingEnabled(true)
            setCrashReportingEnabled(true)
            settingsPreferencesStore.markCrashReportingPromptSeen()
        }
    }

    fun onDecline() {
        viewModelScope.launch {
            settingsPreferencesStore.markCrashReportingPromptSeen()
        }
    }
}
