package dev.odaridavid.smoovie.settings

import platform.Foundation.NSBundle

actual fun appVersionInfo(): AppVersion {
    val info = NSBundle.mainBundle.infoDictionary
    return AppVersion(
        name = (info?.get("CFBundleShortVersionString") as? String) ?: "?",
        code = (info?.get("CFBundleVersion") as? String) ?: "?",
    )
}
