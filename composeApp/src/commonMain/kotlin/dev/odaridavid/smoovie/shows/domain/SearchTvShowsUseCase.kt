package dev.odaridavid.smoovie.shows.domain

import dev.odaridavid.smoovie.shows.TvShowUiMapper

class SearchTvShowsUseCase(
    private val repository: TvShowsRepository,
    private val mapper: TvShowUiMapper,
) {
    suspend operator fun invoke(
        query: String,
        page: Int = 1,
    ): TvShowsPage {
        val response = repository.searchTvShows(query, page)
        return TvShowsPage(
            tvShows = mapper.toUiModels(response.results),
            page = response.page,
            totalPages = response.totalPages,
        )
    }
}
