package dev.odaridavid.smoovie

import android.app.Application
import dev.odaridavid.smoovie.observability.initLogger
import org.koin.android.ext.koin.androidContext

class SmoovieApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initLogger(isDebug = BuildConfig.DEBUG)
        initKoin {
            androidContext(this@SmoovieApplication)
        }
    }
}
