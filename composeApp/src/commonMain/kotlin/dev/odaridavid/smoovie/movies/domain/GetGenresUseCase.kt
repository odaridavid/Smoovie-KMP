package dev.odaridavid.smoovie.movies.domain

import dev.odaridavid.smoovie.movies.GenreUiModel

class GetGenresUseCase(
    private val repository: MoviesRepository,
) {
    suspend operator fun invoke(): List<GenreUiModel> = repository.getGenres().map { GenreUiModel(it.id, it.name) }
}
