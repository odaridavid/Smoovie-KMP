package dev.odaridavid.smoovie.observability

interface CrashReportingController {
    fun setEnabled(enabled: Boolean)
}

object CrashReportingControllerRegistry {
    var instance: CrashReportingController? = null
}

fun setCrashReportingEnabled(enabled: Boolean) {
    CrashReportingControllerRegistry.instance?.setEnabled(enabled)
}
