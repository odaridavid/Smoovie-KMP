package dev.odaridavid.smoovie.watchlist.data

import dev.odaridavid.smoovie.FakeWatchlistDao
import dev.odaridavid.smoovie.watchlist.domain.MediaType
import dev.odaridavid.smoovie.watchlist.domain.WatchlistEntry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class WatchlistRepositoryImplTest {
    private val movieEntry =
        WatchlistEntry(
            id = 1,
            title = "Interstellar",
            overview = "Space odyssey.",
            releaseDate = "2014",
            voteAverage = "8.6",
            backdropUrl = "https://example.com/back.jpg",
            posterUrl = "https://example.com/poster.jpg",
            mediaType = MediaType.MOVIE,
        )

    private val tvEntry =
        WatchlistEntry(
            id = 1,
            title = "Breaking Bad",
            overview = "Chemistry.",
            releaseDate = "2008",
            voteAverage = "9.5",
            backdropUrl = null,
            posterUrl = null,
            mediaType = MediaType.TV,
        )

    @Test
    fun `given empty dao - when observeAll - then emits empty list`() =
        runTest {
            val repo = WatchlistRepositoryImpl(FakeWatchlistDao()) { 0L }

            assertEquals(emptyList(), repo.observeAll().first())
        }

    @Test
    fun `given entry not present - when toggle - then entry is inserted`() =
        runTest {
            val repo = WatchlistRepositoryImpl(FakeWatchlistDao()) { 1000L }

            repo.toggle(movieEntry)

            val list = repo.observeAll().first()
            assertEquals(1, list.size)
            assertEquals(movieEntry, list[0])
        }

    @Test
    fun `given entry already present - when toggle - then entry is removed`() =
        runTest {
            val repo = WatchlistRepositoryImpl(FakeWatchlistDao()) { 1000L }
            repo.toggle(movieEntry)

            repo.toggle(movieEntry)

            assertEquals(emptyList(), repo.observeAll().first())
        }

    @Test
    fun `given entry - when remove - then entry is deleted`() =
        runTest {
            val repo = WatchlistRepositoryImpl(FakeWatchlistDao()) { 1000L }
            repo.toggle(movieEntry)

            repo.remove(movieEntry.id, movieEntry.mediaType)

            assertEquals(emptyList(), repo.observeAll().first())
        }

    @Test
    fun `given missing entry - when remove - then is a no-op`() =
        runTest {
            val repo = WatchlistRepositoryImpl(FakeWatchlistDao()) { 0L }

            repo.remove(999, MediaType.MOVIE)

            assertEquals(emptyList(), repo.observeAll().first())
        }

    @Test
    fun `given entry present - when observeContains - then emits true`() =
        runTest {
            val repo = WatchlistRepositoryImpl(FakeWatchlistDao()) { 1000L }
            repo.toggle(movieEntry)

            assertTrue(repo.observeContains(movieEntry.id, movieEntry.mediaType).first())
        }

    @Test
    fun `given entry missing - when observeContains - then emits false`() =
        runTest {
            val repo = WatchlistRepositoryImpl(FakeWatchlistDao()) { 0L }

            assertFalse(repo.observeContains(movieEntry.id, movieEntry.mediaType).first())
        }

    @Test
    fun `given movie and tv share id - when observeContains - then they don't collide`() =
        runTest {
            val repo = WatchlistRepositoryImpl(FakeWatchlistDao()) { 1000L }
            repo.toggle(movieEntry)

            assertTrue(repo.observeContains(movieEntry.id, MediaType.MOVIE).first())
            assertFalse(repo.observeContains(tvEntry.id, MediaType.TV).first())
        }

    @Test
    fun `given multiple entries inserted at different times - when observeAll - then ordered newest first`() =
        runTest {
            val dao = FakeWatchlistDao()
            var clock = 1_000L
            val repo = WatchlistRepositoryImpl(dao) { clock }

            repo.toggle(movieEntry)
            clock = 2_000L
            repo.toggle(movieEntry.copy(id = 2, title = "Inception"))
            clock = 3_000L
            repo.toggle(movieEntry.copy(id = 3, title = "Tenet"))

            val list = repo.observeAll().first()
            assertEquals(listOf(3, 2, 1), list.map { it.id })
        }

    @Test
    fun `given entry inserted - when observed - then maps entity fields back to domain`() =
        runTest {
            val repo = WatchlistRepositoryImpl(FakeWatchlistDao()) { 1000L }

            repo.toggle(movieEntry)

            val recovered = repo.observeAll().first().single()
            assertEquals(movieEntry, recovered)
        }

    @Test
    fun `given tv entry - when toggled - then mediaType is preserved across round trip`() =
        runTest {
            val repo = WatchlistRepositoryImpl(FakeWatchlistDao()) { 1000L }

            repo.toggle(tvEntry)

            val recovered = repo.observeAll().first().single()
            assertEquals(MediaType.TV, recovered.mediaType)
        }
}
