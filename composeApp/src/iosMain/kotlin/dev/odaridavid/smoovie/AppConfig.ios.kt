package dev.odaridavid.smoovie

import platform.Foundation.NSBundle

actual val tmdbApiKey: String
    get() = NSBundle.mainBundle.objectForInfoDictionaryKey("TMDB_ACCESS_TOKEN") as? String ?: ""
