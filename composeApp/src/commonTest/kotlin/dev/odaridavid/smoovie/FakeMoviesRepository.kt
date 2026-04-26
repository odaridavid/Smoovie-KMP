package dev.odaridavid.smoovie

import dev.odaridavid.smoovie.movies.data.Genre
import dev.odaridavid.smoovie.movies.data.KeywordsResponse
import dev.odaridavid.smoovie.movies.data.Movie
import dev.odaridavid.smoovie.movies.data.MovieDetail
import dev.odaridavid.smoovie.movies.data.MoviesResponse
import dev.odaridavid.smoovie.movies.data.WatchProvidersResponse
import dev.odaridavid.smoovie.movies.domain.MoviesRepository

class FakeMoviesRepository(
    var movies: List<Movie> = emptyList(),
    var discoverMovies: List<Movie> = emptyList(),
    var trendingMovies: List<Movie> = emptyList(),
    var movieDetail: MovieDetail? = null,
    var watchProviders: WatchProvidersResponse = WatchProvidersResponse(id = 0),
    var keywords: KeywordsResponse = KeywordsResponse(id = 0),
    var error: Exception? = null,
    var genresError: Exception? = null,
    var totalPages: Int = 1,
    var genres: List<Genre> = emptyList(),
) : MoviesRepository {
    override suspend fun getPopularMovies(page: Int): MoviesResponse {
        error?.let { throw it }
        return MoviesResponse(
            page = page,
            results = movies,
            totalPages = totalPages,
            totalResults = movies.size,
        )
    }

    override suspend fun searchMovies(
        query: String,
        page: Int,
    ): MoviesResponse {
        error?.let { throw it }
        return MoviesResponse(
            page = page,
            results = movies,
            totalPages = totalPages,
            totalResults = movies.size,
        )
    }

    override suspend fun discoverMoviesByGenre(
        genreId: Int,
        page: Int,
    ): MoviesResponse {
        error?.let { throw it }
        return MoviesResponse(
            page = page,
            results = discoverMovies,
            totalPages = totalPages,
            totalResults = discoverMovies.size,
        )
    }

    override suspend fun getGenres(): List<Genre> {
        genresError?.let { throw it }
        return genres
    }

    override suspend fun getMovieDetail(movieId: Int): MovieDetail {
        error?.let { throw it }
        return movieDetail ?: error("No movie detail configured")
    }

    override suspend fun getWatchProviders(movieId: Int): WatchProvidersResponse {
        error?.let { throw it }
        return watchProviders
    }

    override suspend fun getMovieKeywords(movieId: Int): KeywordsResponse {
        error?.let { throw it }
        return keywords
    }

    override suspend fun getTrendingMovies(): MoviesResponse {
        error?.let { throw it }
        return MoviesResponse(page = 1, results = trendingMovies, totalPages = 1, totalResults = trendingMovies.size)
    }
}
