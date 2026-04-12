package dev.odaridavid.smoovie.movies

import dev.odaridavid.smoovie.FakeConfigurationRepository
import dev.odaridavid.smoovie.FakeMoviesRepository
import dev.odaridavid.smoovie.configuration.ConfigurationRepository
import dev.odaridavid.smoovie.configuration.ConfigurationStore
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
class MoviesViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    private val testMovies =
        listOf(
            Movie(id = 1, title = "Interstellar", overview = "Space odyssey.", voteAverage = 8.6),
            Movie(id = 2, title = "Inception", overview = "Dream heist.", voteAverage = 8.8),
        )

    private val expectedUiModels = testMovies.map { it.toUiModel(backdropUrl = null) }

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel(
        repo: FakeMoviesRepository,
        configRepo: ConfigurationRepository = FakeConfigurationRepository(),
        configStore: ConfigurationStore = ConfigurationStore(),
    ) = MoviesViewModel(repo, configRepo, configStore, MovieUiMapper(configStore))

    @Test
    fun `given api returns movies, when loadMovies is called, then emits success`() =
        runTest {
            val viewModel = buildViewModel(FakeMoviesRepository(movies = testMovies))

            val state = viewModel.uiState.value

            assertIs<MoviesUiState.Success>(state)
            assertEquals(expectedUiModels, state.movies)
        }

    @Test
    fun `given api throws, when loadMovies is called, then emits error`() =
        runTest {
            val viewModel =
                buildViewModel(FakeMoviesRepository(error = Exception("Network error")))

            val state = viewModel.uiState.value

            assertIs<MoviesUiState.Error>(state)
            assertEquals("Network error", state.message)
        }

    @Test
    fun `given exception has no message, when loadMovies is called, then emits fallback message`() =
        runTest {
            val viewModel = buildViewModel(FakeMoviesRepository(error = Exception()))

            val state = viewModel.uiState.value

            assertIs<MoviesUiState.Error>(state)
            assertEquals("Something went wrong", state.message)
        }

    @Test
    fun `given error state, when retry is called, then emits success`() =
        runTest {
            val repo = FakeMoviesRepository(error = Exception("Network error"))
            val viewModel = buildViewModel(repo)
            assertIs<MoviesUiState.Error>(viewModel.uiState.value)

            repo.error = null
            repo.movies = testMovies
            viewModel.loadMovies()

            val state = viewModel.uiState.value
            assertIs<MoviesUiState.Success>(state)
            assertEquals(expectedUiModels, state.movies)
        }

    @Test
    fun `given configuration api throws, when viewmodel is created, then emits error`() =
        runTest {
            val viewModel =
                buildViewModel(
                    repo = FakeMoviesRepository(),
                    configRepo = FakeConfigurationRepository(error = Exception("Config error")),
                )

            val state = viewModel.uiState.value

            assertIs<MoviesUiState.Error>(state)
            assertEquals("Config error", state.message)
        }
}
