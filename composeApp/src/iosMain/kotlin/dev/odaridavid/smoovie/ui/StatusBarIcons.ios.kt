package dev.odaridavid.smoovie.ui

import androidx.compose.runtime.Composable

// On iOS the status bar style is owned by the hosting UIViewController
// (`preferredStatusBarStyle`). Driving it from Kotlin would need either the deprecated
// `UIApplication.setStatusBarStyle:` (not exposed by the Kotlin/Native bindings) or a
// Swift-side bridge that calls `setNeedsStatusBarAppearanceUpdate()` whenever this value
// changes. Left as a no-op until we wire that up; on iOS the system default already gives
// reasonable contrast in both light and dark mode.
@Composable
actual fun SetStatusBarIcons(useDarkIcons: Boolean) {
    // no-op
}
