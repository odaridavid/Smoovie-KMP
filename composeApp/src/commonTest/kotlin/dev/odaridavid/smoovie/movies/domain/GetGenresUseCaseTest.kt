package dev.odaridavid.smoovie.movies.domain

import dev.odaridavid.smoovie.FakeMoviesRepository
import dev.odaridavid.smoovie.movies.data.Genre
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetGenresUseCaseTest {
    private fun buildUseCase(repo: FakeMoviesRepository) = GetGenresUseCase(repo)

    @Test
    fun `given repository returns genres - when invoked - then returns mapped genre ui models`() =
        runTest {
            val genres =
                listOf(
                    Genre(id = 28, name = "Action"),
                    Genre(id = 35, name = "Comedy"),
                    Genre(id = 18, name = "Drama"),
                )
            val useCase = buildUseCase(FakeMoviesRepository(genres = genres))

            val result = useCase()

            assertEquals(3, result.size)
            assertEquals(28, result[0].id)
            assertEquals("Action", result[0].name)
            assertEquals(35, result[1].id)
            assertEquals("Comedy", result[1].name)
        }

    @Test
    fun `given repository returns empty genres - when invoked - then returns empty list`() =
        runTest {
            val useCase = buildUseCase(FakeMoviesRepository(genres = emptyList()))

            val result = useCase()

            assertTrue(result.isEmpty())
        }

    @Test
    fun `given genres error - when invoked - then propagates exception`() =
        runTest {
            val useCase = buildUseCase(FakeMoviesRepository(genresError = Exception("Genres unavailable")))

            runCatching { useCase() }.also { assertTrue(it.isFailure) }
        }
}
