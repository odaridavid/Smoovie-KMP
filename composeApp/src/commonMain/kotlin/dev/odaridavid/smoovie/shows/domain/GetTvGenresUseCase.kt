package dev.odaridavid.smoovie.shows.domain

import dev.odaridavid.smoovie.shows.TvGenreUiModel

class GetTvGenresUseCase(
    private val repository: TvShowsRepository,
) {
    suspend operator fun invoke(): List<TvGenreUiModel> = repository.getGenres().map { TvGenreUiModel(it.id, it.name) }
}
