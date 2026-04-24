package dev.odaridavid.smoovie.shows.domain

import dev.odaridavid.smoovie.shows.TvShowUiMapper

class GetPopularTvShowsUseCase(
    private val repository: TvShowsRepository,
    private val mapper: TvShowUiMapper,
) {
    suspend operator fun invoke(page: Int = 1): TvShowsPage {
        val response = repository.getPopularTvShows(page)
        return TvShowsPage(
            tvShows = mapper.toUiModels(response.results),
            page = response.page,
            totalPages = response.totalPages,
        )
    }
}
