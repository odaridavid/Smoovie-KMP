package dev.odaridavid.smoovie.settings

data class SettingsUiState(
    val selectedRegion: Region? = null,
    val regions: List<Region> = SUPPORTED_REGIONS,
    val crashReportingEnabled: Boolean = false,
)
