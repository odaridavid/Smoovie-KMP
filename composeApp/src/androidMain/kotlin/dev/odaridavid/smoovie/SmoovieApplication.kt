package dev.odaridavid.smoovie

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dev.odaridavid.smoovie.observability.initLogger
import dev.odaridavid.smoovie.security.AndroidAppCheckTokenProvider
import dev.odaridavid.smoovie.security.AppCheckTokenProviderRegistry
import dev.odaridavid.smoovie.security.appCheckProviderFactory
import org.koin.android.ext.koin.androidContext

class SmoovieApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initLogger(isDebug = BuildConfig.DEBUG)
        initFirebase()
        initKoin {
            androidContext(this@SmoovieApplication)
        }
    }

    private fun initFirebase() {
        FirebaseApp.initializeApp(this)
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(appCheckProviderFactory())
        AppCheckTokenProviderRegistry.instance = AndroidAppCheckTokenProvider()
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
        if (BuildConfig.DEBUG) {
            FirebaseAppCheck.getInstance().getAppCheckToken(false)
        }
    }
}
