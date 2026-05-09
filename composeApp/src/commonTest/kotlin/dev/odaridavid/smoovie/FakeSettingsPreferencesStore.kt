package dev.odaridavid.smoovie

import dev.odaridavid.smoovie.settings.SettingsPreferencesStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeSettingsPreferencesStore(
    initialRegionCode: String? = null,
) : SettingsPreferencesStore {
    private val _regionCode = MutableStateFlow(initialRegionCode)
    override val regionCode: StateFlow<String?> = _regionCode.asStateFlow()

    override suspend fun setRegionCode(code: String?) {
        _regionCode.value = code
    }
}
