package dev.odaridavid.smoovie.settings

import com.russhwolf.settings.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

interface SettingsPreferencesStore {
    val regionCode: StateFlow<String?>

    suspend fun setRegionCode(code: String?)
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

    override suspend fun setRegionCode(code: String?) =
        withContext(Dispatchers.Default) {
            if (code != null) {
                settings.putString(KEY_REGION_CODE, code)
            } else {
                settings.remove(KEY_REGION_CODE)
            }
            _regionCode.value = code
        }

    private companion object {
        const val KEY_REGION_CODE = "settings_region_code"
    }
}
