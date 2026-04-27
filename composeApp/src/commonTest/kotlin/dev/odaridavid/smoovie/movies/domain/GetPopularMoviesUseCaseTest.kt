package dev.odaridavid.smoovie.movies.domain

import dev.odaridavid.smoovie.FakeMoviesRepository
import dev.odaridavid.smoovie.configuration.ConfigurationStore
import dev.odaridavid.smoovie.movies.MovieUiMapper
import dev.odaridavid.smoovie.movies.data.Movie
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetPopularMoviesUseCaseTest {
    private val store = ConfigurationStore()
    private val mapper = MovieUiMapper(store)

    private fun buildUseCase(repo: FakeMoviesRepository) = GetPopularMoviesUseCase(repo, mapper)

    @Test
    fun `given repository returns movies - when invoked - then returns mapped ui models`() =
        runTest {
            val movies =
                listOf(
                    Movie(id = 1, title = "Interstellar", overview = "Space."),
                    Movie(id = 2, title = "Inception", overview = "Dreams."),
                )
            val useCase = buildUseCase(FakeMoviesRepository(movies = movies))

            val result = useCase()

            assertEquals(2, result.movies.size)
            assertEquals("Interstellar", result.movies[0].title)
            assertEquals("Inception", result.movies[1].title)
        }

    @Test
    fun `given repository returns empty list - when invoked - then returns empty movies`() =
        runTest {
            val useCase = buildUseCase(FakeMoviesRepository(movies = emptyList()))

            val result = useCase()

            assertTrue(result.movies.isEmpty())
        }

    @Test
    fun `given repository throws - when invoked - then propagates exception`() =
        runTest {
            val useCase = buildUseCase(FakeMoviesRepository(error = Exception("Network error")))

            runCatching { useCase() }.also { assertTrue(it.isFailure) }
        }

    @Test
    fun `given multiple pages - when invoked with page 2 - then returns correct page info`() =
        runTest {
            val movies = listOf(Movie(id = 3, title = "Tenet", overview = "Time."))
            val useCase = buildUseCase(FakeMoviesRepository(movies = movies, totalPages = 3))

            val result = useCase(page = 2)

            assertEquals(2, result.page)
            assertEquals(3, result.totalPages)
        }

    @Test
    fun `given movie with null poster path - when invoked - then poster url is null`() =
        runTest {
            val movies = listOf(Movie(id = 1, title = "Movie", overview = "", posterPath = null))
            val useCase = buildUseCase(FakeMoviesRepository(movies = movies))

            val result = useCase()

            assertEquals(null, result.movies.first().posterUrl)
        }
}
