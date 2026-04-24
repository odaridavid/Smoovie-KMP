package dev.odaridavid.smoovie.watchlist.domain

import dev.odaridavid.smoovie.FakeWatchlistRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveWatchlistUseCaseTest {
    private val entry =
        WatchlistEntry(
            id = 1,
            title = "Interstellar",
            overview = "Space.",
            releaseDate = "2014",
            voteAverage = "8.6",
            backdropUrl = "back.jpg",
            posterUrl = "poster.jpg",
        )

    @Test
    fun `given empty repo - when invoked - then emits empty list`() =
        runTest {
            val result = ObserveWatchlistUseCase(FakeWatchlistRepository())().first()

            assertEquals(emptyList(), result)
        }

    @Test
    fun `given repo with entries - when invoked - then maps entries to movie ui models`() =
        runTest {
            val repo = FakeWatchlistRepository()
            repo.toggle(entry)

            val result = ObserveWatchlistUseCase(repo)().first()

            assertEquals(1, result.size)
            assertEquals(entry.id, result[0].id)
            assertEquals(entry.title, result[0].title)
            assertEquals(entry.backdropUrl, result[0].backdropUrl)
            assertEquals(entry.posterUrl, result[0].posterUrl)
        }
}
