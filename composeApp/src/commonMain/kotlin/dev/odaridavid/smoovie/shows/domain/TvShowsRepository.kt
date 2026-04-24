package dev.odaridavid.smoovie.shows.domain

import dev.odaridavid.smoovie.shows.data.TvGenre
import dev.odaridavid.smoovie.shows.data.TvShowDetail
import dev.odaridavid.smoovie.shows.data.TvShowsResponse

interface TvShowsRepository {
    suspend fun getPopularTvShows(page: Int = 1): TvShowsResponse

    suspend fun searchTvShows(
        query: String,
        page: Int = 1,
    ): TvShowsResponse

    suspend fun discoverTvShowsByGenre(
        genreId: Int,
        page: Int = 1,
    ): TvShowsResponse

    suspend fun getGenres(): List<TvGenre>

    suspend fun getTvShowDetail(tvShowId: Int): TvShowDetail
}
