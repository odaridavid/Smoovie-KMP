package dev.odaridavid.smoovie

import dev.odaridavid.smoovie.settings.SettingsPreferencesStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeSettingsPreferencesStore(
    initialRegionCode: String? = null,
    initialCrashReportingEnabled: Boolean = false,
    initialHasSeenCrashReportingPrompt: Boolean = false,
) : SettingsPreferencesStore {
    private val _regionCode = MutableStateFlow(initialRegionCode)
    override val regionCode: StateFlow<String?> = _regionCode.asStateFlow()

    private val _crashReportingEnabled = MutableStateFlow(initialCrashReportingEnabled)
    override val crashReportingEnabled: StateFlow<Boolean> = _crashReportingEnabled.asStateFlow()

    private val _hasSeenCrashReportingPrompt = MutableStateFlow(initialHasSeenCrashReportingPrompt)
    override val hasSeenCrashReportingPrompt: StateFlow<Boolean> = _hasSeenCrashReportingPrompt.asStateFlow()

    override suspend fun setRegionCode(code: String?) {
        _regionCode.value = code
    }

    override suspend fun setCrashReportingEnabled(enabled: Boolean) {
        _crashReportingEnabled.value = enabled
    }

    override suspend fun markCrashReportingPromptSeen() {
        _hasSeenCrashReportingPrompt.value = true
    }
}
