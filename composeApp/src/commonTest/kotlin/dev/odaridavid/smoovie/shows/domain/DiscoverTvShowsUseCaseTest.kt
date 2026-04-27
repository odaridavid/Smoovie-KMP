package dev.odaridavid.smoovie.shows.domain

import dev.odaridavid.smoovie.FakeTvShowsRepository
import dev.odaridavid.smoovie.configuration.ConfigurationStore
import dev.odaridavid.smoovie.filter.TvFilterPreferences
import dev.odaridavid.smoovie.filter.TvSortOption
import dev.odaridavid.smoovie.shows.TvShowUiMapper
import dev.odaridavid.smoovie.shows.data.TvShow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DiscoverTvShowsUseCaseTest {
    private val store = ConfigurationStore()
    private val mapper = TvShowUiMapper(store)

    private fun buildUseCase(repo: FakeTvShowsRepository) = DiscoverTvShowsUseCase(repo, mapper)

    @Test
    fun `given default filter - when invoked - then returns mapped discover shows`() =
        runTest {
            val shows = listOf(TvShow(id = 1, name = "Succession", overview = "Roy family."))
            val useCase = buildUseCase(FakeTvShowsRepository(discoverTvShows = shows))

            val result = useCase(filter = TvFilterPreferences())

            assertEquals(1, result.tvShows.size)
            assertEquals("Succession", result.tvShows.first().name)
        }

    @Test
    fun `given genre filter - when invoked - then returns shows from discover endpoint`() =
        runTest {
            val dramaShows = listOf(TvShow(id = 10, name = "The Crown", overview = ""))
            val useCase = buildUseCase(FakeTvShowsRepository(discoverTvShows = dramaShows))

            val result = useCase(filter = TvFilterPreferences(selectedGenreId = 18))

            assertEquals(1, result.tvShows.size)
            assertEquals("The Crown", result.tvShows.first().name)
        }

    @Test
    fun `given rating sort - when invoked - then returns shows from discover endpoint`() =
        runTest {
            val shows = listOf(TvShow(id = 1, name = "High Rated Show", overview = ""))
            val useCase = buildUseCase(FakeTvShowsRepository(discoverTvShows = shows))

            val result = useCase(filter = TvFilterPreferences(sortBy = TvSortOption.RATING))

            assertEquals("High Rated Show", result.tvShows.first().name)
        }

    @Test
    fun `given repository returns empty - when invoked - then returns empty shows`() =
        runTest {
            val useCase = buildUseCase(FakeTvShowsRepository(discoverTvShows = emptyList()))

            val result = useCase(filter = TvFilterPreferences())

            assertTrue(result.tvShows.isEmpty())
        }

    @Test
    fun `given repository throws - when invoked - then propagates exception`() =
        runTest {
            val useCase = buildUseCase(FakeTvShowsRepository(error = Exception("Network error")))

            runCatching { useCase(TvFilterPreferences()) }.also { assertTrue(it.isFailure) }
        }

    @Test
    fun `given multiple pages - when invoked with page 2 - then returns correct page info`() =
        runTest {
            val shows = listOf(TvShow(id = 2, name = "Page 2 Show", overview = ""))
            val useCase = buildUseCase(FakeTvShowsRepository(discoverTvShows = shows, totalPages = 3))

            val result = useCase(filter = TvFilterPreferences(), page = 2)

            assertEquals(2, result.page)
            assertEquals(3, result.totalPages)
        }
}
