package dev.odaridavid.smoovie

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.odaridavid.smoovie.data.TmdbApi
import dev.odaridavid.smoovie.ui.movies.MoviesScreen
import dev.odaridavid.smoovie.ui.movies.MoviesViewModel
import dev.odaridavid.smoovie.ui.theme.SmoovieTheme

@Composable
fun App() {
    SmoovieTheme {
        val viewModel: MoviesViewModel =
            viewModel(
                factory =
                    viewModelFactory {
                        initializer { MoviesViewModel(TmdbApi(apiKey = tmdbApiKey)) }
                    },
            )
        MoviesScreen(viewModel = viewModel)
    }
}