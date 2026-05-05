package dev.odaridavid.smoovie

import androidx.compose.ui.window.ComposeUIViewController
import dev.odaridavid.smoovie.observability.initLogger
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform

@OptIn(ExperimentalNativeApi::class)
@Suppress("ktlint:standard:function-naming")
fun MainViewController() =
    ComposeUIViewController {
        initLogger(isDebug = Platform.isDebugBinary)
        initKoin()
        App()
    }
