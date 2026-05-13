package dev.odaridavid.smoovie.observability

import com.google.firebase.crashlytics.FirebaseCrashlytics
import dev.odaridavid.smoovie.BuildConfig

class AndroidCrashReportingController : CrashReportingController {
    override fun setEnabled(enabled: Boolean) {
        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = enabled && !BuildConfig.DEBUG
    }
}
