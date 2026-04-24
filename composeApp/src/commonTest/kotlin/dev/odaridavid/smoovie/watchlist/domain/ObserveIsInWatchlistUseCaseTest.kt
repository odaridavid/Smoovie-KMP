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
        )

    @Test
    fun `given movie not in watchlist - when invoked - then emits false`() =
        runTest {
            val result = ObserveIsInWatchlistUseCase(FakeWatchlistRepository())(entry.id).first()

            assertFalse(result)
        }

    @Test
    fun `given movie in watchlist - when invoked - then emits true`() =
        runTest {
            val repo = FakeWatchlistRepository()
            repo.toggle(entry)

            val result = ObserveIsInWatchlistUseCase(repo)(entry.id).first()

            assertTrue(result)
        }
}
