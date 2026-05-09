package dev.odaridavid.smoovie.settings

import java.util.Locale

actual fun systemRegionCode(): String? = Locale.getDefault().country.takeIf { it.isNotBlank() }
