package dev.odaridavid.smoovie.shows.domain

import dev.odaridavid.smoovie.shared.data.WatchProvidersResponse
import dev.odaridavid.smoovie.shows.data.SeasonDetail
import dev.odaridavid.smoovie.shows.data.TvGenre
import dev.odaridavid.smoovie.shows.data.TvKeywordsResponse
import dev.odaridavid.smoovie.shows.data.TvShowDetail
import dev.odaridavid.smoovie.shows.data.TvShowsResponse

interface TvShowsRepository {
    suspend fun getPopularTvShows(page: Int = 1): TvShowsResponse

    suspend fun searchTvShows(
        query: String,
        page: Int = 1,
    ): TvShowsResponse

    suspend fun discoverTvShows(
        genreId: Int?,
        sortBy: String,
        minRating: Float,
        page: Int = 1,
    ): TvShowsResponse

    suspend fun getGenres(): List<TvGenre>

    suspend fun getTvShowDetail(tvShowId: Int): TvShowDetail

    suspend fun getSeasonDetail(
        tvShowId: Int,
        seasonNumber: Int,
    ): SeasonDetail

    suspend fun getWatchProviders(tvShowId: Int): WatchProvidersResponse

    suspend fun getKeywords(tvShowId: Int): TvKeywordsResponse
}
