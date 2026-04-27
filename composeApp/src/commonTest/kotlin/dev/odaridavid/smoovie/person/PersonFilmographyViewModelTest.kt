package dev.odaridavid.smoovie.person

import dev.odaridavid.smoovie.FakePersonRepository
import dev.odaridavid.smoovie.configuration.ConfigurationStore
import dev.odaridavid.smoovie.person.data.MovieCredits
import dev.odaridavid.smoovie.person.data.PersonDetail
import dev.odaridavid.smoovie.person.data.PersonMovieCredit
import dev.odaridavid.smoovie.person.data.PersonTvCredit
import dev.odaridavid.smoovie.person.data.TvCredits
import dev.odaridavid.smoovie.person.domain.GetPersonDetailUseCase
import dev.odaridavid.smoovie.utils.AppError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class PersonFilmographyViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    private val testPersonDetail =
        PersonDetail(
            id = 7,
            name = "Christoph Waltz",
            biography = "Austrian actor.",
            birthday = "1956-10-04",
            knownForDepartment = "Acting",
            movieCredits =
                MovieCredits(
                    cast =
                        listOf(
                            PersonMovieCredit(
                                id = 1,
                                title = "Inglourious Basterds",
                                character = "Landa",
                                popularity = 70.0,
                                releaseDate = "2009-08-19",
                                voteAverage = 8.3,
                            ),
                            PersonMovieCredit(
                                id = 2,
                                title = "Django Unchained",
                                character = "Schultz",
                                popularity = 60.0,
                                releaseDate = "2012-12-25",
                                voteAverage = 8.4,
                            ),
                        ),
                ),
        )

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel(
        repo: FakePersonRepository,
        configStore: ConfigurationStore = ConfigurationStore(),
    ) = PersonFilmographyViewModel(testPersonDetail.id, GetPersonDetailUseCase(repo, configStore))

    @Test
    fun `given api returns person detail - when viewmodel is created - then emits success`() =
        runTest {
            val viewModel = buildViewModel(FakePersonRepository(personDetail = testPersonDetail))

            val state = viewModel.uiState.value

            assertIs<PersonFilmographyUiState.Success>(state)
            assertEquals("Christoph Waltz", state.personDetail.name)
        }

    @Test
    fun `given person with movie credits - when viewmodel is created - then filmography is populated`() =
        runTest {
            val viewModel = buildViewModel(FakePersonRepository(personDetail = testPersonDetail))

            val state = viewModel.uiState.value as PersonFilmographyUiState.Success

            assertEquals(2, state.personDetail.movieFilmography.size)
            assertEquals(listOf(1, 2), state.personDetail.movieFilmography.map { it.movie.id })
        }

    @Test
    fun `given person with tv credits - when viewmodel is created - then tv filmography is populated`() =
        runTest {
            val detail =
                testPersonDetail.copy(
                    tvCredits =
                        TvCredits(
                            cast =
                                listOf(
                                    PersonTvCredit(
                                        id = 100,
                                        name = "Some Show",
                                        character = "Villain",
                                        popularity = 30.0,
                                        firstAirDate = "2015-01-01",
                                        voteAverage = 7.5,
                                    ),
                                ),
                        ),
                )
            val viewModel = buildViewModel(FakePersonRepository(personDetail = detail))

            val state = viewModel.uiState.value as PersonFilmographyUiState.Success

            assertEquals(1, state.personDetail.tvFilmography.size)
            assertEquals(
                "Some Show",
                state.personDetail.tvFilmography
                    .first()
                    .tvShow.name,
            )
        }

    @Test
    fun `given api throws - when viewmodel is created - then emits error`() =
        runTest {
            val viewModel = buildViewModel(FakePersonRepository(error = Exception("Network error")))

            val state = viewModel.uiState.value

            assertIs<PersonFilmographyUiState.Error>(state)
            assertEquals(AppError.NetworkError, state.error)
        }

    @Test
    fun `given untyped exception - when viewmodel is created - then emits network error`() =
        runTest {
            val viewModel = buildViewModel(FakePersonRepository(error = Exception()))

            val state = viewModel.uiState.value

            assertIs<PersonFilmographyUiState.Error>(state)
            assertEquals(AppError.NetworkError, state.error)
        }

    @Test
    fun `given error state - when load is called - then emits success`() =
        runTest {
            val repo = FakePersonRepository(error = Exception("Network error"))
            val viewModel = buildViewModel(repo)
            assertIs<PersonFilmographyUiState.Error>(viewModel.uiState.value)

            repo.error = null
            repo.personDetail = testPersonDetail
            viewModel.load()

            val state = viewModel.uiState.value
            assertIs<PersonFilmographyUiState.Success>(state)
            assertEquals("Christoph Waltz", state.personDetail.name)
        }

    @Test
    fun `given success state - when load is called again with new data - then emits updated detail`() =
        runTest {
            val repo = FakePersonRepository(personDetail = testPersonDetail)
            val viewModel = buildViewModel(repo)
            assertIs<PersonFilmographyUiState.Success>(viewModel.uiState.value)

            repo.personDetail = testPersonDetail.copy(name = "Updated Name")
            viewModel.load()

            val state = viewModel.uiState.value
            assertIs<PersonFilmographyUiState.Success>(state)
            assertEquals("Updated Name", state.personDetail.name)
        }
}
