package dev.odaridavid.smoovie.ui

import androidx.compose.runtime.Composable

/**
 * Controls the status bar icon appearance for the current screen.
 *
 * @param useDarkIcons true → dark icons (suitable for light backgrounds);
 *                     false → light/white icons (suitable for dark backgrounds and hero images).
 *
 * Each screen should call this once per composition; the value is applied to the host window
 * via a SideEffect, so it stays in effect until the next caller updates it.
 */
@Composable
expect fun SetStatusBarIcons(useDarkIcons: Boolean)
