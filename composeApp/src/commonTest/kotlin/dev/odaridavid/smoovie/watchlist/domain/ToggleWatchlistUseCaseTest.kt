package dev.odaridavid.smoovie.watchlist.domain

import dev.odaridavid.smoovie.FakeWatchlistRepository
import dev.odaridavid.smoovie.movies.MovieUiModel
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

    @Test
    fun `given movie not in watchlist - when toggled - then movie is added`() =
        runTest {
            val repo = FakeWatchlistRepository()

            ToggleWatchlistUseCase(repo)(movie)

            assertTrue(repo.observeContains(movie.id).first())
        }

    @Test
    fun `given movie in watchlist - when toggled - then movie is removed`() =
        runTest {
            val repo = FakeWatchlistRepository()
            val useCase = ToggleWatchlistUseCase(repo)
            useCase(movie)

            useCase(movie)

            assertFalse(repo.observeContains(movie.id).first())
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
        }
}
