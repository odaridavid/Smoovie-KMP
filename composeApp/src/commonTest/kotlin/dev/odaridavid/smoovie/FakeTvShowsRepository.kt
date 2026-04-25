package dev.odaridavid.smoovie

import dev.odaridavid.smoovie.movies.data.WatchProvidersResponse
import dev.odaridavid.smoovie.shows.data.SeasonDetail
import dev.odaridavid.smoovie.shows.data.TvGenre
import dev.odaridavid.smoovie.shows.data.TvShow
import dev.odaridavid.smoovie.shows.data.TvShowDetail
import dev.odaridavid.smoovie.shows.data.TvShowsResponse
import dev.odaridavid.smoovie.shows.domain.TvShowsRepository

class FakeTvShowsRepository(
    var tvShows: List<TvShow> = emptyList(),
    var discoverTvShows: List<TvShow> = emptyList(),
    var tvShowDetail: TvShowDetail? = null,
    var seasonDetail: SeasonDetail? = null,
    var watchProviders: WatchProvidersResponse = WatchProvidersResponse(id = 0),
    var error: Exception? = null,
    var genresError: Exception? = null,
    var totalPages: Int = 1,
    var genres: List<TvGenre> = emptyList(),
) : TvShowsRepository {
    override suspend fun getPopularTvShows(page: Int): TvShowsResponse {
        error?.let { throw it }
        return TvShowsResponse(
            page = page,
            results = tvShows,
            totalPages = totalPages,
            totalResults = tvShows.size,
        )
    }

    override suspend fun searchTvShows(
        query: String,
        page: Int,
    ): TvShowsResponse {
        error?.let { throw it }
        return TvShowsResponse(
            page = page,
            results = tvShows,
            totalPages = totalPages,
            totalResults = tvShows.size,
        )
    }

    override suspend fun discoverTvShowsByGenre(
        genreId: Int,
        page: Int,
    ): TvShowsResponse {
        error?.let { throw it }
        return TvShowsResponse(
            page = page,
            results = discoverTvShows,
            totalPages = totalPages,
            totalResults = discoverTvShows.size,
        )
    }

    override suspend fun getGenres(): List<TvGenre> {
        genresError?.let { throw it }
        return genres
    }

    override suspend fun getTvShowDetail(tvShowId: Int): TvShowDetail {
        error?.let { throw it }
        return tvShowDetail ?: error("No tv show detail configured")
    }

    override suspend fun getSeasonDetail(
        tvShowId: Int,
        seasonNumber: Int,
    ): SeasonDetail {
        error?.let { throw it }
        return seasonDetail ?: error("No season detail configured")
    }

    override suspend fun getWatchProviders(tvShowId: Int): WatchProvidersResponse {
        error?.let { throw it }
        return watchProviders
    }
}
