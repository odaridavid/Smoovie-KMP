package dev.odaridavid.smoovie.settings

import dev.odaridavid.smoovie.BuildConfig

actual fun appVersionInfo(): AppVersion =
    AppVersion(
        name = BuildConfig.VERSION_NAME,
        code = BuildConfig.VERSION_CODE.toString(),
    )
