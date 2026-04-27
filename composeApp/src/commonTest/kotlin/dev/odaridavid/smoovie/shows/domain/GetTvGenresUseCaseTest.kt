package dev.odaridavid.smoovie.shows.domain

import dev.odaridavid.smoovie.FakeTvShowsRepository
import dev.odaridavid.smoovie.shows.data.TvGenre
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetTvGenresUseCaseTest {
    private fun buildUseCase(repo: FakeTvShowsRepository) = GetTvGenresUseCase(repo)

    @Test
    fun `given repository returns genres - when invoked - then returns mapped genre ui models`() =
        runTest {
            val genres =
                listOf(
                    TvGenre(id = 10759, name = "Action & Adventure"),
                    TvGenre(id = 16, name = "Animation"),
                    TvGenre(id = 35, name = "Comedy"),
                )
            val useCase = buildUseCase(FakeTvShowsRepository(genres = genres))

            val result = useCase()

            assertEquals(3, result.size)
            assertEquals(10759, result[0].id)
            assertEquals("Action & Adventure", result[0].name)
            assertEquals(16, result[1].id)
            assertEquals("Animation", result[1].name)
        }

    @Test
    fun `given repository returns empty genres - when invoked - then returns empty list`() =
        runTest {
            val useCase = buildUseCase(FakeTvShowsRepository(genres = emptyList()))

            val result = useCase()

            assertTrue(result.isEmpty())
        }

    @Test
    fun `given genres error - when invoked - then propagates exception`() =
        runTest {
            val useCase = buildUseCase(FakeTvShowsRepository(genresError = Exception("Genres unavailable")))

            runCatching { useCase() }.also { assertTrue(it.isFailure) }
        }
}
