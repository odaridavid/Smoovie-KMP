package dev.odaridavid.smoovie.person.domain

import dev.odaridavid.smoovie.FakePersonRepository
import dev.odaridavid.smoovie.configuration.ConfigurationStore
import dev.odaridavid.smoovie.person.data.MovieCredits
import dev.odaridavid.smoovie.person.data.PersonDetail
import dev.odaridavid.smoovie.person.data.PersonMovieCredit
import dev.odaridavid.smoovie.person.data.PersonTvCredit
import dev.odaridavid.smoovie.person.data.TvCredits
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetPersonDetailUseCaseTest {
    private val baseDetail =
        PersonDetail(
            id = 1,
            name = "Cillian Murphy",
            biography = "  Irish actor.  ",
            birthday = "1976-05-25",
            placeOfBirth = "Douglas, Cork, Ireland",
            knownForDepartment = "Acting",
        )

    private fun buildUseCase(
        repo: FakePersonRepository,
        store: ConfigurationStore = ConfigurationStore(),
    ) = GetPersonDetailUseCase(repo, store)

    @Test
    fun `given repository returns person - when invoked - then maps name and biography`() =
        runTest {
            val useCase = buildUseCase(FakePersonRepository(personDetail = baseDetail))

            val result = useCase(1)

            assertEquals("Cillian Murphy", result.name)
            assertEquals("Irish actor.", result.biography)
        }

    @Test
    fun `given person with birthday - when invoked - then formats birthday as readable date`() =
        runTest {
            val useCase = buildUseCase(FakePersonRepository(personDetail = baseDetail))

            val result = useCase(1)

            assertEquals("25 May 1976", result.birthday)
        }

    @Test
    fun `given person with null birthday - when invoked - then birthday is empty string`() =
        runTest {
            val detail = baseDetail.copy(birthday = null)
            val useCase = buildUseCase(FakePersonRepository(personDetail = detail))

            val result = useCase(1)

            assertEquals("", result.birthday)
        }

    @Test
    fun `given person with movie credits - when invoked - then filmography is sorted by popularity descending`() =
        runTest {
            val detail =
                baseDetail.copy(
                    movieCredits =
                        MovieCredits(
                            cast =
                                listOf(
                                    PersonMovieCredit(
                                        id = 1,
                                        title = "Oppenheimer",
                                        popularity = 80.0,
                                        character = "Oppenheimer",
                                        releaseDate = "2023-07-21",
                                        voteAverage = 8.3,
                                    ),
                                    PersonMovieCredit(
                                        id = 2,
                                        title = "Peaky Blinders",
                                        popularity = 20.0,
                                        character = "Tommy",
                                        releaseDate = "2013-01-01",
                                        voteAverage = 8.8,
                                    ),
                                ),
                        ),
                )
            val useCase = buildUseCase(FakePersonRepository(personDetail = detail))

            val result = useCase(1)

            assertEquals(listOf(1, 2), result.movieFilmography.map { it.movie.id })
        }

    @Test
    fun `given person with tv credits - when invoked - then tv filmography is populated`() =
        runTest {
            val detail =
                baseDetail.copy(
                    tvCredits =
                        TvCredits(
                            cast =
                                listOf(
                                    PersonTvCredit(
                                        id = 100,
                                        name = "Peaky Blinders",
                                        popularity = 50.0,
                                        character = "Tommy Shelby",
                                        firstAirDate = "2013-01-01",
                                        voteAverage = 8.8,
                                    ),
                                ),
                        ),
                )
            val useCase = buildUseCase(FakePersonRepository(personDetail = detail))

            val result = useCase(1)

            assertEquals(1, result.tvFilmography.size)
            assertEquals(
                "Peaky Blinders",
                result.tvFilmography
                    .first()
                    .tvShow.name,
            )
        }

    @Test
    fun `given person with no credits - when invoked - then filmography lists are empty`() =
        runTest {
            val useCase = buildUseCase(FakePersonRepository(personDetail = baseDetail))

            val result = useCase(1)

            assertTrue(result.movieFilmography.isEmpty())
            assertTrue(result.tvFilmography.isEmpty())
        }

    @Test
    fun `given repository throws - when invoked - then propagates exception`() =
        runTest {
            val useCase = buildUseCase(FakePersonRepository(error = Exception("Network error")))

            runCatching { useCase(1) }.also { assertTrue(it.isFailure) }
        }
}
