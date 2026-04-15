package dev.odaridavid.smoovie

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.odaridavid.smoovie.movies.MovieDetailScreen
import dev.odaridavid.smoovie.movies.MovieDetailViewModel
import dev.odaridavid.smoovie.movies.MovieUiModel
import dev.odaridavid.smoovie.movies.MoviesScreen
import dev.odaridavid.smoovie.movies.MoviesViewModel
import dev.odaridavid.smoovie.theme.SmoovieTheme
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

sealed interface Screen {
    data object MovieList : Screen

    data class MovieDetail(
        val movieId: Int,
        val movie: MovieUiModel,
    ) : Screen
}

@Composable
fun App() {
    SmoovieTheme {
        Surface {
            var currentScreen by remember { mutableStateOf<Screen>(Screen.MovieList) }
            val moviesViewModel: MoviesViewModel = koinViewModel()

            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    if (targetState is Screen.MovieDetail) {
                        (slideInHorizontally { it } + fadeIn()) togetherWith
                            (slideOutHorizontally { -it / 3 } + fadeOut())
                    } else {
                        (slideInHorizontally { -it / 3 } + fadeIn()) togetherWith
                            (slideOutHorizontally { it } + fadeOut())
                    }
                },
                label = "screen_transition",
            ) { screen ->
                when (screen) {
                    is Screen.MovieList -> {
                        MoviesScreen(
                            viewModel = moviesViewModel,
                            onMovieClick = { movie ->
                                currentScreen = Screen.MovieDetail(
                                    movieId = movie.id,
                                    movie = movie,
                                )
                            },
                        )
                    }

                    is Screen.MovieDetail -> {
                        val detailViewModel: MovieDetailViewModel = koinViewModel(
                            key = screen.movieId.toString(),
                            parameters = { parametersOf(screen.movieId) },
                        )
                        MovieDetailScreen(
                            viewModel = detailViewModel,
                            movie = screen.movie,
                            onBack = { currentScreen = Screen.MovieList },
                        )
                    }
                }
            }
        }
    }
}
