package dev.odaridavid.smoovie.ui

import androidx.compose.runtime.Composable

@Composable
expect fun SearchBackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
)
