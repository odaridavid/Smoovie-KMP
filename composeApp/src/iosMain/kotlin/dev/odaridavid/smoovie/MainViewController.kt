package dev.odaridavid.smoovie

import androidx.compose.ui.window.ComposeUIViewController

@Suppress("ktlint:standard:function-naming")
fun MainViewController() =
    ComposeUIViewController {
        initKoin()
        App()
    }
