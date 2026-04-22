package dev.odaridavid.smoovie

import android.app.Application
import org.koin.android.ext.koin.androidContext

class SmoovieApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@SmoovieApplication)
        }
    }
}
