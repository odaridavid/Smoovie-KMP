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
class PersonDetailViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    private val testPersonDetail =
        PersonDetail(
            id = 42,
            name = "Matthew McConaughey",
            biography = "  Bio with whitespace.  ",
            birthday = "1969-11-04",
            placeOfBirth = "Uvalde, Texas, USA",
            knownForDepartment = "Acting",
            profilePath = null,
            movieCredits =
                MovieCredits(
                    cast =
                        listOf(
                            credit(
                                id = 1,
                                title = "Interstellar",
                                character = "Cooper",
                                popularity = 50.0,
                            ),
                            credit(
                                id = 2,
                                title = "Dallas Buyers Club",
                                character = "Ron",
                                popularity = 30.0,
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
    ) = PersonDetailViewModel(testPersonDetail.id, GetPersonDetailUseCase(repo, configStore))

    @Test
    fun `given api returns person detail - when viewmodel is created - then emits success with mapped fields`() =
        runTest {
            val repo = FakePersonRepository(personDetail = testPersonDetail)
            val viewModel = buildViewModel(repo)

            val state = viewModel.uiState.value

            assertIs<PersonDetailUiState.Success>(state)
            assertEquals("Matthew McConaughey", state.personDetail.name)
            assertEquals("Bio with whitespace.", state.personDetail.biography)
            assertEquals("4 Nov 1969", state.personDetail.birthday)
            assertEquals("Uvalde, Texas, USA", state.personDetail.placeOfBirth)
            assertEquals("Acting", state.personDetail.knownForDepartment)
        }

    @Test
    fun `given person with cast credits - when viewmodel is created - then movie filmography is sorted by popularity`() =
        runTest {
            val repo = FakePersonRepository(personDetail = testPersonDetail)
            val viewModel = buildViewModel(repo)

            val state = viewModel.uiState.value

            assertIs<PersonDetailUiState.Success>(state)
            assertEquals(listOf(1, 2), state.personDetail.movieFilmography.map { it.movie.id })
            assertEquals(
                "Cooper",
                state.personDetail.movieFilmography
                    .first()
                    .role,
            )
            assertEquals(emptyList(), state.personDetail.tvFilmography)
        }

    @Test
    fun `given person with tv credits - when viewmodel is created - then tv filmography is populated separately`() =
        runTest {
            val withTvCredits =
                testPersonDetail.copy(
                    tvCredits =
                        TvCredits(
                            cast =
                                listOf(
                                    tvCredit(id = 100, name = "True Detective", character = "Rust", popularity = 80.0),
                                    tvCredit(id = 200, name = "Yellowstone", character = "Cameo", popularity = 5.0),
                                ),
                        ),
                )
            val repo = FakePersonRepository(personDetail = withTvCredits)
            val viewModel = buildViewModel(repo)

            val state = viewModel.uiState.value as PersonDetailUiState.Success
            assertEquals(listOf(100, 200), state.personDetail.tvFilmography.map { it.tvShow.id })
            assertEquals(
                "True Detective",
                state.personDetail.tvFilmography
                    .first()
                    .tvShow.name,
            )
            assertEquals(
                "Rust",
                state.personDetail.tvFilmography
                    .first()
                    .role,
            )
            assertEquals(listOf(1, 2), state.personDetail.movieFilmography.map { it.movie.id })
        }

    @Test
    fun `given movie and tv credits with same id - when mapped - then both retained in their respective lists`() =
        runTest {
            val withColliding =
                testPersonDetail.copy(
                    tvCredits = TvCredits(cast = listOf(tvCredit(id = 1, name = "Show With Colliding ID", popularity = 10.0))),
                )
            val repo = FakePersonRepository(personDetail = withColliding)
            val viewModel = buildViewModel(repo)

            val state = viewModel.uiState.value as PersonDetailUiState.Success
            assertEquals(true, state.personDetail.movieFilmography.any { it.movie.id == 1 })
            assertEquals(true, state.personDetail.tvFilmography.any { it.tvShow.id == 1 })
        }

    @Test
    fun `given api throws - when viewmodel is created - then emits error`() =
        runTest {
            val repo = FakePersonRepository(error = Exception("Network error"))
            val viewModel = buildViewModel(repo)

            val state = viewModel.uiState.value

            assertIs<PersonDetailUiState.Error>(state)
            assertEquals(AppError.NetworkError, state.error)
        }

    @Test
    fun `given untyped exception - when viewmodel is created - then emits network error`() =
        runTest {
            val repo = FakePersonRepository(error = Exception())
            val viewModel = buildViewModel(repo)

            val state = viewModel.uiState.value

            assertIs<PersonDetailUiState.Error>(state)
            assertEquals(AppError.NetworkError, state.error)
        }

    @Test
    fun `given error state - when retry is called - then emits success`() =
        runTest {
            val repo = FakePersonRepository(error = Exception("Network error"))
            val viewModel = buildViewModel(repo)
            assertIs<PersonDetailUiState.Error>(viewModel.uiState.value)

            repo.error = null
            repo.personDetail = testPersonDetail
            viewModel.loadPersonDetail()

            val state = viewModel.uiState.value
            assertIs<PersonDetailUiState.Success>(state)
            assertEquals("Matthew McConaughey", state.personDetail.name)
        }

    @Test
    fun `given success state - when retry is called with new data - then emits latest detail`() =
        runTest {
            val repo = FakePersonRepository(personDetail = testPersonDetail)
            val viewModel = buildViewModel(repo)
            assertIs<PersonDetailUiState.Success>(viewModel.uiState.value)

            repo.personDetail =
                testPersonDetail.copy(
                    name = "Updated Name",
                    biography = "Updated bio.",
                )
            viewModel.loadPersonDetail()

            val state = viewModel.uiState.value
            assertIs<PersonDetailUiState.Success>(state)
            assertEquals("Updated Name", state.personDetail.name)
            assertEquals("Updated bio.", state.personDetail.biography)
        }

    private fun credit(
        id: Int,
        title: String = "Movie $id",
        character: String = "",
        popularity: Double = 10.0,
    ) = PersonMovieCredit(
        id = id,
        title = title,
        character = character,
        popularity = popularity,
        releaseDate = "2023-01-01",
        voteAverage = 7.0,
    )

    private fun tvCredit(
        id: Int,
        name: String = "Show $id",
        character: String = "",
        popularity: Double = 10.0,
    ) = PersonTvCredit(
        id = id,
        name = name,
        character = character,
        popularity = popularity,
        firstAirDate = "2020-01-01",
        voteAverage = 8.0,
    )
}
