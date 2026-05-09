package dev.odaridavid.smoovie.settings

import platform.Foundation.NSLocale
import platform.Foundation.countryCode
import platform.Foundation.currentLocale

actual fun systemRegionCode(): String? = NSLocale.currentLocale.countryCode?.takeIf { it.isNotBlank() }
