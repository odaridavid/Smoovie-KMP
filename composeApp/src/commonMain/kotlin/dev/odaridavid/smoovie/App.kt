package dev.odaridavid.smoovie

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dev.odaridavid.smoovie.movies.MovieDetailScreen
import dev.odaridavid.smoovie.movies.MovieDetailViewModel
import dev.odaridavid.smoovie.movies.MoviesScreen
import dev.odaridavid.smoovie.movies.MoviesViewModel
import dev.odaridavid.smoovie.person.PersonDetailScreen
import dev.odaridavid.smoovie.person.PersonDetailViewModel
import dev.odaridavid.smoovie.theme.SmoovieTheme
import dev.odaridavid.smoovie.watchlist.WatchlistScreen
import dev.odaridavid.smoovie.watchlist.WatchlistViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private const val TRANSITION_DURATION_MS = 500
private val TransitionEasing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)

@Composable
fun App() {
    SmoovieTheme {
        Surface {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = MoviesRoute,
                enterTransition = {
                    slideInHorizontally(tween(TRANSITION_DURATION_MS, easing = TransitionEasing)) { it }
                },
                exitTransition = {
                    slideOutHorizontally(tween(TRANSITION_DURATION_MS, easing = TransitionEasing)) { -it / 3 }
                },
                popEnterTransition = {
                    slideInHorizontally(tween(TRANSITION_DURATION_MS, easing = TransitionEasing)) { -it / 3 }
                },
                popExitTransition = {
                    slideOutHorizontally(tween(TRANSITION_DURATION_MS, easing = TransitionEasing)) { it }
                },
            ) {
                composable<MoviesRoute> {
                    val viewModel: MoviesViewModel = koinViewModel()
                    MoviesScreen(
                        viewModel = viewModel,
                        onMovieClick = { movie -> navController.navigate(movie.toRoute()) },
                        onWatchlistClick = { navController.navigate(WatchlistRoute) },
                    )
                }

                composable<WatchlistRoute> {
                    val viewModel: WatchlistViewModel = koinViewModel()
                    WatchlistScreen(
                        viewModel = viewModel,
                        onBack = { navController.navigateUp() },
                        onMovieClick = { movie -> navController.navigate(movie.toRoute()) },
                    )
                }

                composable<MovieDetailRoute> { entry ->
                    val route: MovieDetailRoute = entry.toRoute()
                    val viewModel: MovieDetailViewModel =
                        koinViewModel(
                            key = route.id.toString(),
                            parameters = { parametersOf(route.id) },
                        )
                    MovieDetailScreen(
                        viewModel = viewModel,
                        movie = route.toUiModel(),
                        onBack = { navController.navigateUp() },
                        onMovieClick = { movie -> navController.navigate(movie.toRoute()) },
                        onPersonClick = { person -> navController.navigate(person.toRoute()) },
                    )
                }

                composable<PersonDetailRoute> { entry ->
                    val route: PersonDetailRoute = entry.toRoute()
                    val viewModel: PersonDetailViewModel =
                        koinViewModel(
                            key = "person_${route.id}",
                            parameters = { parametersOf(route.id) },
                        )
                    PersonDetailScreen(
                        viewModel = viewModel,
                        person = route.toUiModel(),
                        onBack = { navController.navigateUp() },
                        onMovieClick = { movie -> navController.navigate(movie.toRoute()) },
                    )
                }
            }
        }
    }
}
