package dev.odaridavid.smoovie.settings

import com.russhwolf.settings.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

interface SettingsPreferencesStore {
    val regionCode: StateFlow<String?>
    val crashReportingEnabled: StateFlow<Boolean>
    val hasSeenCrashReportingPrompt: StateFlow<Boolean>

    suspend fun setRegionCode(code: String?)

    suspend fun setCrashReportingEnabled(enabled: Boolean)

    suspend fun markCrashReportingPromptSeen()
}

class SettingsPreferencesStoreImpl(
    private val settings: Settings,
    systemRegionProvider: () -> String? = ::systemRegionCode,
) : SettingsPreferencesStore {
    private val _regionCode: MutableStateFlow<String?> =
        MutableStateFlow(
            settings.getStringOrNull(KEY_REGION_CODE)
                ?: resolveRegion(systemRegionProvider())?.code,
        )

    override val regionCode: StateFlow<String?> = _regionCode.asStateFlow()

    private val _crashReportingEnabled: MutableStateFlow<Boolean> =
        MutableStateFlow(settings.getBoolean(KEY_CRASH_REPORTING_ENABLED, defaultValue = false))

    override val crashReportingEnabled: StateFlow<Boolean> = _crashReportingEnabled.asStateFlow()

    private val _hasSeenCrashReportingPrompt: MutableStateFlow<Boolean> =
        MutableStateFlow(settings.getBoolean(KEY_CRASH_REPORTING_PROMPT_SEEN, defaultValue = false))

    override val hasSeenCrashReportingPrompt: StateFlow<Boolean> = _hasSeenCrashReportingPrompt.asStateFlow()

    override suspend fun setRegionCode(code: String?) =
        withContext(Dispatchers.Default) {
            if (code != null) {
                settings.putString(KEY_REGION_CODE, code)
            } else {
                settings.remove(KEY_REGION_CODE)
            }
            _regionCode.value = code
        }

    override suspend fun setCrashReportingEnabled(enabled: Boolean) =
        withContext(Dispatchers.Default) {
            settings.putBoolean(KEY_CRASH_REPORTING_ENABLED, enabled)
            _crashReportingEnabled.value = enabled
        }

    override suspend fun markCrashReportingPromptSeen() =
        withContext(Dispatchers.Default) {
            settings.putBoolean(KEY_CRASH_REPORTING_PROMPT_SEEN, true)
            _hasSeenCrashReportingPrompt.value = true
        }

    private companion object {
        const val KEY_REGION_CODE = "settings_region_code"
        const val KEY_CRASH_REPORTING_ENABLED = "settings_crash_reporting_enabled"
        const val KEY_CRASH_REPORTING_PROMPT_SEEN = "settings_crash_reporting_prompt_seen"
    }
}
