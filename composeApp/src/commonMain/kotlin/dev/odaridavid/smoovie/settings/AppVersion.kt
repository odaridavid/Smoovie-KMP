package dev.odaridavid.smoovie.settings

data class AppVersion(
    val name: String,
    val code: String,
)

expect fun appVersionInfo(): AppVersion
