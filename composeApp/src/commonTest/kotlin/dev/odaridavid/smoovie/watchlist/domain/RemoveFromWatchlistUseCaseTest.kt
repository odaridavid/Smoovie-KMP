package dev.odaridavid.smoovie.watchlist.domain

import dev.odaridavid.smoovie.FakeWatchlistRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RemoveFromWatchlistUseCaseTest {
    private val movieEntry =
        WatchlistEntry(
            id = 42,
            title = "Interstellar",
            overview = "",
            releaseDate = "",
            voteAverage = "",
            backdropUrl = null,
            posterUrl = null,
            mediaType = MediaType.MOVIE,
        )

    private val tvEntry = movieEntry.copy(mediaType = MediaType.TV, title = "Breaking Bad")

    @Test
    fun `given movie in watchlist - when invoked - then movie is removed`() =
        runTest {
            val repo = FakeWatchlistRepository()
            repo.toggle(movieEntry)

            RemoveFromWatchlistUseCase(repo)(movieEntry.id, MediaType.MOVIE)

            assertFalse(repo.observeContains(movieEntry.id, MediaType.MOVIE).first())
        }

    @Test
    fun `given movie not in watchlist - when invoked - then is a no-op`() =
        runTest {
            val repo = FakeWatchlistRepository()

            RemoveFromWatchlistUseCase(repo)(movieEntry.id, MediaType.MOVIE)

            assertFalse(repo.observeContains(movieEntry.id, MediaType.MOVIE).first())
        }

    @Test
    fun `given tv and movie share id - when removing movie - then tv stays`() =
        runTest {
            val repo = FakeWatchlistRepository()
            repo.toggle(movieEntry)
            repo.toggle(tvEntry)

            RemoveFromWatchlistUseCase(repo)(movieEntry.id, MediaType.MOVIE)

            assertFalse(repo.observeContains(movieEntry.id, MediaType.MOVIE).first())
            assertTrue(repo.observeContains(tvEntry.id, MediaType.TV).first())
        }
}
