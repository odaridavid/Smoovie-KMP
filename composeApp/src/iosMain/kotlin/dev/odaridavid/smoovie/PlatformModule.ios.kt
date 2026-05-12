package dev.odaridavid.smoovie

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import dev.odaridavid.smoovie.storage.DatabaseBuilderFactory
import dev.odaridavid.smoovie.utils.AppReviewRequester
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

actual val platformModule: Module =
    module {
        single { DatabaseBuilderFactory() }
        single<Settings> { NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults) }
        single<AppReviewRequester> { IosAppReviewRequester() }
    }
