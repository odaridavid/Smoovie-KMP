package dev.odaridavid.smoovie.watchlist.domain

import dev.odaridavid.smoovie.FakeWatchlistRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveIsInWatchlistUseCaseTest {
    private val entry =
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

    @Test
    fun `given movie not in watchlist - when invoked - then emits false`() =
        runTest {
            val result =
                ObserveIsInWatchlistUseCase(FakeWatchlistRepository())(entry.id, MediaType.MOVIE).first()

            assertFalse(result)
        }

    @Test
    fun `given movie in watchlist - when invoked - then emits true`() =
        runTest {
            val repo = FakeWatchlistRepository()
            repo.toggle(entry)

            val result = ObserveIsInWatchlistUseCase(repo)(entry.id, MediaType.MOVIE).first()

            assertTrue(result)
        }

    @Test
    fun `given movie in watchlist - when checking tv with same id - then emits false`() =
        runTest {
            val repo = FakeWatchlistRepository()
            repo.toggle(entry)

            val result = ObserveIsInWatchlistUseCase(repo)(entry.id, MediaType.TV).first()

            assertFalse(result)
        }
}
