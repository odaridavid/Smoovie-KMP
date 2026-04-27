package dev.odaridavid.smoovie.movies.domain

import dev.odaridavid.smoovie.FakeMoviesRepository
import dev.odaridavid.smoovie.configuration.ConfigurationStore
import dev.odaridavid.smoovie.movies.MovieUiMapper
import dev.odaridavid.smoovie.movies.data.Movie
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SearchMoviesUseCaseTest {
    private val store = ConfigurationStore()
    private val mapper = MovieUiMapper(store)

    private fun buildUseCase(repo: FakeMoviesRepository) = SearchMoviesUseCase(repo, mapper)

    @Test
    fun `given repository returns results - when invoked with query - then returns mapped movies`() =
        runTest {
            val movies =
                listOf(
                    Movie(id = 1, title = "Interstellar", overview = "Space."),
                )
            val useCase = buildUseCase(FakeMoviesRepository(movies = movies))

            val result = useCase(query = "interstellar")

            assertEquals(1, result.movies.size)
            assertEquals("Interstellar", result.movies.first().title)
        }

    @Test
    fun `given repository returns empty - when invoked - then returns empty movies`() =
        runTest {
            val useCase = buildUseCase(FakeMoviesRepository(movies = emptyList()))

            val result = useCase(query = "unknown")

            assertTrue(result.movies.isEmpty())
        }

    @Test
    fun `given repository throws - when invoked - then propagates exception`() =
        runTest {
            val useCase = buildUseCase(FakeMoviesRepository(error = Exception("Network error")))

            runCatching { useCase(query = "anything") }.also { assertTrue(it.isFailure) }
        }

    @Test
    fun `given multiple pages - when invoked with page 2 - then returns correct page info`() =
        runTest {
            val movies = listOf(Movie(id = 5, title = "Page 2 Movie", overview = ""))
            val useCase = buildUseCase(FakeMoviesRepository(movies = movies, totalPages = 5))

            val result = useCase(query = "movie", page = 2)

            assertEquals(2, result.page)
            assertEquals(5, result.totalPages)
        }
}
