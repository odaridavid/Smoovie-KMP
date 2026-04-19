package dev.odaridavid.smoovie.movies.domain

import dev.odaridavid.smoovie.movies.MovieUiModel

data class MoviesPage(
    val movies: List<MovieUiModel>,
    val page: Int,
    val totalPages: Int,
)
