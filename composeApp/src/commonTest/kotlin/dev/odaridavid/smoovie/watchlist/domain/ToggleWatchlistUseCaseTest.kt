package dev.odaridavid.smoovie.watchlist.domain

import dev.odaridavid.smoovie.FakeWatchlistRepository
import dev.odaridavid.smoovie.movies.MovieUiModel
import dev.odaridavid.smoovie.shows.TvShowUiModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ToggleWatchlistUseCaseTest {
    private val movie =
        MovieUiModel(
            id = 42,
            title = "Interstellar",
            overview = "Space.",
            releaseDate = "2014",
            voteAverage = "8.6",
            backdropUrl = "back.jpg",
            posterUrl = "poster.jpg",
        )

    private val tvShow =
        TvShowUiModel(
            id = 42,
            name = "Breaking Bad",
            overview = "Chemistry.",
            firstAirDate = "2008",
            voteAverage = "9.5",
            backdropUrl = "tv_back.jpg",
            posterUrl = "tv_poster.jpg",
        )

    @Test
    fun `given movie not in watchlist - when toggled - then movie is added`() =
        runTest {
            val repo = FakeWatchlistRepository()

            ToggleWatchlistUseCase(repo)(movie)

            assertTrue(repo.observeContains(movie.id, MediaType.MOVIE).first())
        }

    @Test
    fun `given movie in watchlist - when toggled - then movie is removed`() =
        runTest {
            val repo = FakeWatchlistRepository()
            val useCase = ToggleWatchlistUseCase(repo)
            useCase(movie)

            useCase(movie)

            assertFalse(repo.observeContains(movie.id, MediaType.MOVIE).first())
        }

    @Test
    fun `given movie added - when observed - then stored fields match source`() =
        runTest {
            val repo = FakeWatchlistRepository()

            ToggleWatchlistUseCase(repo)(movie)

            val stored = repo.observeAll().first().single()
            assertEquals(movie.id, stored.id)
            assertEquals(movie.title, stored.title)
            assertEquals(movie.overview, stored.overview)
            assertEquals(movie.backdropUrl, stored.backdropUrl)
            assertEquals(movie.posterUrl, stored.posterUrl)
            assertEquals(MediaType.MOVIE, stored.mediaType)
        }

    @Test
    fun `given tv show not in watchlist - when toggled - then tv show is added`() =
        runTest {
            val repo = FakeWatchlistRepository()

            ToggleWatchlistUseCase(repo)(tvShow)

            assertTrue(repo.observeContains(tvShow.id, MediaType.TV).first())
        }

    @Test
    fun `given movie and tv share id - when both toggled - then both stored independently`() =
        runTest {
            val repo = FakeWatchlistRepository()
            val useCase = ToggleWatchlistUseCase(repo)

            useCase(movie)
            useCase(tvShow)

            assertTrue(repo.observeContains(movie.id, MediaType.MOVIE).first())
            assertTrue(repo.observeContains(tvShow.id, MediaType.TV).first())
            assertEquals(2, repo.observeAll().first().size)
        }

    @Test
    fun `given tv show added - when observed - then name maps to title and firstAirDate to releaseDate`() =
        runTest {
            val repo = FakeWatchlistRepository()

            ToggleWatchlistUseCase(repo)(tvShow)

            val stored = repo.observeAll().first().single()
            assertEquals(tvShow.name, stored.title)
            assertEquals(tvShow.firstAirDate, stored.releaseDate)
            assertEquals(MediaType.TV, stored.mediaType)
        }
}
