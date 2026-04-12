package dev.odaridavid.smoovie

import android.app.Application
import dev.odaridavid.smoovie.di.initKoin

class SmoovieApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}
