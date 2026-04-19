package dev.odaridavid.smoovie.movies

import dev.odaridavid.smoovie.FakeMoviesRepository
import dev.odaridavid.smoovie.configuration.ConfigurationStore
import dev.odaridavid.smoovie.movies.data.Genre
import dev.odaridavid.smoovie.movies.data.MovieDetail
import dev.odaridavid.smoovie.movies.domain.GetMovieDetailUseCase
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
class MovieDetailViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    private val testMovieDetail =
        MovieDetail(
            id = 1,
            title = "Interstellar",
            overview = "Space odyssey.",
            releaseDate = "2014-11-05",
            voteAverage = 8.6,
            voteCount = 34521,
            runtime = 169,
            tagline = "Mankind was born on Earth.",
            genres = listOf(Genre(1, "Adventure"), Genre(2, "Drama")),
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
        repo: FakeMoviesRepository,
        configStore: ConfigurationStore = ConfigurationStore(),
    ) = MovieDetailViewModel(testMovieDetail.id, GetMovieDetailUseCase(repo, configStore))

    @Test
    fun `given api returns movie detail - when viewmodel is created - then emits success`() =
        runTest {
            val repo = FakeMoviesRepository(movieDetail = testMovieDetail)
            val viewModel = buildViewModel(repo)

            val state = viewModel.uiState.value

            assertIs<MovieDetailUiState.Success>(state)
            assertEquals("Interstellar", state.movieDetail.title)
            assertEquals("2h 49m", state.movieDetail.runtime)
            assertEquals("Adventure, Drama", state.movieDetail.genres)
            assertEquals("Mankind was born on Earth.", state.movieDetail.tagline)
        }

    @Test
    fun `given api throws - when viewmodel is created - then emits error`() =
        runTest {
            val repo = FakeMoviesRepository(error = Exception("Network error"))
            val viewModel = buildViewModel(repo)

            val state = viewModel.uiState.value

            assertIs<MovieDetailUiState.Error>(state)
            assertEquals("Network error", state.message)
        }

    @Test
    fun `given exception has no message - when viewmodel is created - then emits fallback message`() =
        runTest {
            val repo = FakeMoviesRepository(error = Exception())
            val viewModel = buildViewModel(repo)

            val state = viewModel.uiState.value

            assertIs<MovieDetailUiState.Error>(state)
            assertEquals("Something went wrong", state.message)
        }

    @Test
    fun `given error state - when retry is called - then emits success`() =
        runTest {
            val repo = FakeMoviesRepository(error = Exception("Network error"))
            val viewModel = buildViewModel(repo)
            assertIs<MovieDetailUiState.Error>(viewModel.uiState.value)

            repo.error = null
            repo.movieDetail = testMovieDetail
            viewModel.loadMovieDetail()

            val state = viewModel.uiState.value
            assertIs<MovieDetailUiState.Success>(state)
            assertEquals("Interstellar", state.movieDetail.title)
        }
}
