package dev.odaridavid.smoovie.shows.domain

import dev.odaridavid.smoovie.FakeTvShowsRepository
import dev.odaridavid.smoovie.configuration.ConfigurationStore
import dev.odaridavid.smoovie.shows.TvShowUiMapper
import dev.odaridavid.smoovie.shows.data.TvShow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetPopularTvShowsUseCaseTest {
    private val store = ConfigurationStore()
    private val mapper = TvShowUiMapper(store)

    private fun buildUseCase(repo: FakeTvShowsRepository) = GetPopularTvShowsUseCase(repo, mapper)

    @Test
    fun `given repository returns tv shows - when invoked - then returns mapped ui models`() =
        runTest {
            val shows =
                listOf(
                    TvShow(id = 1, name = "Breaking Bad", overview = "Chemistry."),
                    TvShow(id = 2, name = "The Wire", overview = "Baltimore."),
                )
            val useCase = buildUseCase(FakeTvShowsRepository(tvShows = shows))

            val result = useCase()

            assertEquals(2, result.tvShows.size)
            assertEquals("Breaking Bad", result.tvShows[0].name)
            assertEquals("The Wire", result.tvShows[1].name)
        }

    @Test
    fun `given repository returns empty list - when invoked - then returns empty tv shows`() =
        runTest {
            val useCase = buildUseCase(FakeTvShowsRepository(tvShows = emptyList()))

            val result = useCase()

            assertTrue(result.tvShows.isEmpty())
        }

    @Test
    fun `given repository throws - when invoked - then propagates exception`() =
        runTest {
            val useCase = buildUseCase(FakeTvShowsRepository(error = Exception("Network error")))

            runCatching { useCase() }.also { assertTrue(it.isFailure) }
        }

    @Test
    fun `given multiple pages - when invoked with page 2 - then returns correct page info`() =
        runTest {
            val shows = listOf(TvShow(id = 3, name = "Succession", overview = ""))
            val useCase = buildUseCase(FakeTvShowsRepository(tvShows = shows, totalPages = 4))

            val result = useCase(page = 2)

            assertEquals(2, result.page)
            assertEquals(4, result.totalPages)
        }
}
