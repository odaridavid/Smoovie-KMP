package dev.odaridavid.smoovie.shows.domain

import dev.odaridavid.smoovie.shows.TvShowUiMapper

class GetTvShowsByGenreUseCase(
    private val repository: TvShowsRepository,
    private val mapper: TvShowUiMapper,
) {
    suspend operator fun invoke(
        genreId: Int,
        page: Int = 1,
    ): TvShowsPage {
        val response = repository.discoverTvShowsByGenre(genreId, page)
        return TvShowsPage(
            tvShows = mapper.toUiModels(response.results),
            page = response.page,
            totalPages = response.totalPages,
        )
    }
}
