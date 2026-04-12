package dev.odaridavid.smoovie

import androidx.compose.ui.window.ComposeUIViewController
import dev.odaridavid.smoovie.di.initKoin

fun MainViewController() =
    ComposeUIViewController {
        initKoin()
        App()
    }
