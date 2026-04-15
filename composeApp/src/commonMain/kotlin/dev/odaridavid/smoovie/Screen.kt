package dev.odaridavid.smoovie

import dev.odaridavid.smoovie.movies.MovieUiModel

sealed interface Screen {
    data object MovieList : Screen

    data class MovieDetail(
        val movieId: Int,
        val movie: MovieUiModel,
    ) : Screen
}
