package dev.odaridavid.smoovie.shows.domain

import dev.odaridavid.smoovie.FakeTvShowsRepository
import dev.odaridavid.smoovie.configuration.ConfigurationStore
import dev.odaridavid.smoovie.shows.TvShowUiMapper
import dev.odaridavid.smoovie.shows.data.TvShow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SearchTvShowsUseCaseTest {
    private val store = ConfigurationStore()
    private val mapper = TvShowUiMapper(store)

    private fun buildUseCase(repo: FakeTvShowsRepository) = SearchTvShowsUseCase(repo, mapper)

    @Test
    fun `given repository returns results - when invoked with query - then returns mapped shows`() =
        runTest {
            val shows = listOf(TvShow(id = 1, name = "Breaking Bad", overview = "Chemistry."))
            val useCase = buildUseCase(FakeTvShowsRepository(tvShows = shows))

            val result = useCase(query = "breaking")

            assertEquals(1, result.tvShows.size)
            assertEquals("Breaking Bad", result.tvShows.first().name)
        }

    @Test
    fun `given repository returns empty - when invoked - then returns empty shows`() =
        runTest {
            val useCase = buildUseCase(FakeTvShowsRepository(tvShows = emptyList()))

            val result = useCase(query = "unknown")

            assertTrue(result.tvShows.isEmpty())
        }

    @Test
    fun `given repository throws - when invoked - then propagates exception`() =
        runTest {
            val useCase = buildUseCase(FakeTvShowsRepository(error = Exception("Network error")))

            runCatching { useCase(query = "anything") }.also { assertTrue(it.isFailure) }
        }

    @Test
    fun `given multiple pages - when invoked with page 2 - then returns correct page info`() =
        runTest {
            val shows = listOf(TvShow(id = 5, name = "Page 2 Show", overview = ""))
            val useCase = buildUseCase(FakeTvShowsRepository(tvShows = shows, totalPages = 5))

            val result = useCase(query = "show", page = 2)

            assertEquals(2, result.page)
            assertEquals(5, result.totalPages)
        }
}
