package dev.odaridavid.smoovie

import dev.odaridavid.smoovie.observability.CrashReportingController

class FakeCrashReportingController : CrashReportingController {
    val calls = mutableListOf<Boolean>()

    override fun setEnabled(enabled: Boolean) {
        calls += enabled
    }
}
