package dev.odaridavid.smoovie.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import org.jetbrains.compose.resources.Font
import smoovie.composeapp.generated.resources.Res
import smoovie.composeapp.generated.resources.lobster_regular

val LobsterFontFamily
    @Composable get() = FontFamily(Font(Res.font.lobster_regular))
