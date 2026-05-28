package dev.odaridavid.smoovie

import android.app.Application
import androidx.appfunctions.service.AppFunctionConfiguration
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import dev.odaridavid.smoovie.appfunctions.SmoovieAppFunctions
import dev.odaridavid.smoovie.observability.AndroidCrashReportingController
import dev.odaridavid.smoovie.observability.CrashReportingControllerRegistry
import dev.odaridavid.smoovie.observability.initLogger
import dev.odaridavid.smoovie.security.AndroidAppCheckTokenProvider
import dev.odaridavid.smoovie.security.AppCheckTokenProviderRegistry
import dev.odaridavid.smoovie.security.appCheckProviderFactory
import org.koin.android.ext.koin.androidContext
import org.koin.mp.KoinPlatform

class SmoovieApplication :
    Application(),
    AppFunctionConfiguration.Provider {
    override fun onCreate() {
        super.onCreate()
        initLogger(isDebug = BuildConfig.DEBUG)
        initFirebase()
        initKoin {
            androidContext(this@SmoovieApplication)
        }
    }

    override val appFunctionConfiguration: AppFunctionConfiguration
        get() =
            AppFunctionConfiguration
                .Builder()
                .addEnclosingClassFactory(SmoovieAppFunctions::class.java) {
                    KoinPlatform.getKoin().get<SmoovieAppFunctions>()
                }.build()

    private fun initFirebase() {
        FirebaseApp.initializeApp(this)
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(appCheckProviderFactory())
        AppCheckTokenProviderRegistry.instance = AndroidAppCheckTokenProvider()
        CrashReportingControllerRegistry.instance = AndroidCrashReportingController()
    }
}
