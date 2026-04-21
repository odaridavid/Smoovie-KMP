package dev.odaridavid.smoovie

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import dev.odaridavid.smoovie.movies.MovieDetailScreen
import dev.odaridavid.smoovie.movies.MovieDetailViewModel
import dev.odaridavid.smoovie.movies.MovieUiModel
import dev.odaridavid.smoovie.movies.MoviesScreen
import dev.odaridavid.smoovie.movies.MoviesViewModel
import dev.odaridavid.smoovie.person.PersonDetailScreen
import dev.odaridavid.smoovie.person.PersonDetailViewModel
import dev.odaridavid.smoovie.person.PersonSummaryUiModel
import dev.odaridavid.smoovie.theme.SmoovieTheme
import dev.odaridavid.smoovie.ui.LocalAnimatedVisibilityScope
import dev.odaridavid.smoovie.ui.LocalSharedTransitionScope
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun App() {
    SmoovieTheme {
        Surface {
            val backStack = remember { mutableStateListOf<Screen>(Screen.MovieList) }
            var isForward by remember { mutableStateOf(true) }
            val currentScreen = backStack.last()

            val push: (Screen) -> Unit = { screen ->
                isForward = true
                backStack.add(screen)
            }
            val pop: () -> Unit = {
                if (backStack.size > 1) {
                    isForward = false
                    backStack.removeAt(backStack.lastIndex)
                }
            }

            SharedTransitionLayout {
                CompositionLocalProvider(LocalSharedTransitionScope provides this) {
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            val easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
                            if (isForward) {
                                slideInHorizontally(tween(500, easing = easing)) { it } togetherWith
                                    slideOutHorizontally(tween(500, easing = easing)) { -it / 3 }
                            } else {
                                slideInHorizontally(tween(500, easing = easing)) { -it / 3 } togetherWith
                                    slideOutHorizontally(tween(500, easing = easing)) { it }
                            }
                        },
                        label = "screen_transition",
                    ) { screen ->
                        CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                            SetupNavigation(
                                currentScreen = screen,
                                onMovieClick = { movie ->
                                    push(Screen.MovieDetail(movieId = movie.id, movie = movie))
                                },
                                onPersonClick = { person ->
                                    push(Screen.PersonDetail(personId = person.id, person = person))
                                },
                                onBack = pop,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SetupNavigation(
    currentScreen: Screen,
    onMovieClick: (MovieUiModel) -> Unit,
    onPersonClick: (PersonSummaryUiModel) -> Unit,
    onBack: () -> Unit,
) {
    when (currentScreen) {
        is Screen.MovieList -> {
            val moviesViewModel: MoviesViewModel = koinViewModel()
            MoviesScreen(
                viewModel = moviesViewModel,
                onMovieClick = { movie -> onMovieClick(movie) },
            )
        }

        is Screen.MovieDetail -> {
            val detailViewModel: MovieDetailViewModel =
                koinViewModel(
                    key = currentScreen.movieId.toString(),
                    parameters = { parametersOf(currentScreen.movieId) },
                )
            MovieDetailScreen(
                viewModel = detailViewModel,
                movie = currentScreen.movie,
                onBack = onBack,
                onMovieClick = onMovieClick,
                onPersonClick = onPersonClick,
            )
        }

        is Screen.PersonDetail -> {
            val personViewModel: PersonDetailViewModel =
                koinViewModel(
                    key = "person_${currentScreen.personId}",
                    parameters = { parametersOf(currentScreen.personId) },
                )
            PersonDetailScreen(
                viewModel = personViewModel,
                person = currentScreen.person,
                onBack = onBack,
                onMovieClick = onMovieClick,
            )
        }
    }
}
