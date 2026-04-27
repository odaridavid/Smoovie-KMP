package dev.odaridavid.smoovie.movies.domain

import dev.odaridavid.smoovie.FakeMoviesRepository
import dev.odaridavid.smoovie.configuration.ConfigurationStore
import dev.odaridavid.smoovie.movies.MovieUiMapper
import dev.odaridavid.smoovie.movies.data.Movie
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetTrendingMoviesUseCaseTest {
    private val store = ConfigurationStore()
    private val mapper = MovieUiMapper(store)

    private fun buildUseCase(repo: FakeMoviesRepository) = GetTrendingMoviesUseCase(repo, mapper)

    @Test
    fun `given repository returns trending movies - when invoked - then returns mapped ui models`() =
        runTest {
            val trending =
                listOf(
                    Movie(id = 10, title = "Top Gun: Maverick", overview = "Jets."),
                    Movie(id = 11, title = "Everything Everywhere All at Once", overview = "Multiverse."),
                )
            val useCase = buildUseCase(FakeMoviesRepository(trendingMovies = trending))

            val result = useCase()

            assertEquals(2, result.size)
            assertEquals("Top Gun: Maverick", result[0].title)
            assertEquals("Everything Everywhere All at Once", result[1].title)
        }

    @Test
    fun `given repository returns empty trending - when invoked - then returns empty list`() =
        runTest {
            val useCase = buildUseCase(FakeMoviesRepository(trendingMovies = emptyList()))

            val result = useCase()

            assertTrue(result.isEmpty())
        }

    @Test
    fun `given repository throws - when invoked - then propagates exception`() =
        runTest {
            val useCase = buildUseCase(FakeMoviesRepository(error = Exception("Network error")))

            runCatching { useCase() }.also { assertTrue(it.isFailure) }
        }
}
