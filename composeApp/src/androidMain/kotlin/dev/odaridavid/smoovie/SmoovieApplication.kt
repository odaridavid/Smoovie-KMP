package dev.odaridavid.smoovie

import android.app.Application

class SmoovieApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}
