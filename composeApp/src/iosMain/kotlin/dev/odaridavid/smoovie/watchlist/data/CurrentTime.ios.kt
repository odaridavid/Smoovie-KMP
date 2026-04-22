package dev.odaridavid.smoovie.watchlist.data

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

internal actual fun currentTimeMillis(): Long = (NSDate().timeIntervalSince1970 * 1_000).toLong()
