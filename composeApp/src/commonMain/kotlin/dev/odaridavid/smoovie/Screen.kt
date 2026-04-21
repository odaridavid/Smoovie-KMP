package dev.odaridavid.smoovie

import dev.odaridavid.smoovie.movies.MovieUiModel
import dev.odaridavid.smoovie.person.PersonSummaryUiModel

sealed interface Screen {
    data object MovieList : Screen

    data class MovieDetail(
        val movieId: Int,
        val movie: MovieUiModel,
    ) : Screen

    data class PersonDetail(
        val personId: Int,
        val person: PersonSummaryUiModel,
    ) : Screen
}
