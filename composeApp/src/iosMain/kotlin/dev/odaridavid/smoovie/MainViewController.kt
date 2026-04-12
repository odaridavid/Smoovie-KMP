package dev.odaridavid.smoovie

import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() =
    ComposeUIViewController {
        initKoin()
        App()
    }
