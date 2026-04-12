package dev.odaridavid.smoovie

import androidx.compose.runtime.Composable
import dev.odaridavid.smoovie.ui.movies.MoviesScreen
import dev.odaridavid.smoovie.ui.movies.MoviesViewModel
import dev.odaridavid.smoovie.ui.theme.SmoovieTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    SmoovieTheme {
        val viewModel: MoviesViewModel = koinViewModel()
        MoviesScreen(viewModel = viewModel)
    }
}
