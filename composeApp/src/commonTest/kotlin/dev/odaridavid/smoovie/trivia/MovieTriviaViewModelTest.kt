package dev.odaridavid.smoovie.trivia

import dev.odaridavid.smoovie.FakeMoviesRepository
import dev.odaridavid.smoovie.movies.data.Genre
import dev.odaridavid.smoovie.movies.data.MovieDetail
import dev.odaridavid.smoovie.trivia.domain.GenerateMovieTriviaUseCase
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MovieTriviaViewModelTest {
    private val movieDetail =
        MovieDetail(
            id = 1,
            title = "Interstellar",
            overview = "",
            releaseDate = "2014-11-05",
            runtime = 169,
            genres = listOf(Genre(id = 18, name = "Drama"), Genre(id = 878, name = "Science Fiction")),
        )

    private val genrePool =
        movieDetail.genres +
            listOf(
                Genre(id = 35, name = "Comedy"),
                Genre(id = 27, name = "Horror"),
                Genre(id = 37, name = "Western"),
            )

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel(
        detail: MovieDetail? = movieDetail,
        genres: List<Genre> = genrePool,
    ): MovieTriviaViewModel {
        val repository = FakeMoviesRepository(movieDetail = detail, genres = genres)
        return MovieTriviaViewModel(movieId = 1, generateTrivia = GenerateMovieTriviaUseCase(repository))
    }

    @Test
    fun `given trivia available - when initialised - then state is content`() =
        runTest {
            val viewModel = buildViewModel()

            val state = viewModel.uiState.value
            assertTrue(state is TriviaUiState.Content)
            assertTrue(state.questions.isNotEmpty())
        }

    @Test
    fun `given no trivia - when initialised - then state is empty`() =
        runTest {
            val viewModel =
                buildViewModel(
                    detail =
                        MovieDetail(id = 1, title = "Mystery", overview = "", releaseDate = "", runtime = null),
                    genres = emptyList(),
                )

            assertEquals(TriviaUiState.Empty, viewModel.uiState.value)
        }

    @Test
    fun `given a correct answer - when selected - then score increments`() =
        runTest {
            val viewModel = buildViewModel()
            val content = viewModel.uiState.value as TriviaUiState.Content

            viewModel.selectAnswer(content.currentQuestion.correctIndex)

            val updated = viewModel.uiState.value as TriviaUiState.Content
            assertEquals(1, updated.score)
            assertTrue(updated.isAnswered)
        }

    @Test
    fun `given an answered question - when answering again - then selection is ignored`() =
        runTest {
            val viewModel = buildViewModel()
            val content = viewModel.uiState.value as TriviaUiState.Content
            val wrongIndex = (content.currentQuestion.correctIndex + 1) % content.currentQuestion.options.size

            viewModel.selectAnswer(content.currentQuestion.correctIndex)
            viewModel.selectAnswer(wrongIndex)

            val updated = viewModel.uiState.value as TriviaUiState.Content
            assertEquals(1, updated.score)
            assertEquals(content.currentQuestion.correctIndex, updated.selectedOptionIndex)
        }

    @Test
    fun `given the last question answered - when advancing - then quiz finishes`() =
        runTest {
            val viewModel = buildViewModel()

            repeat(MAX_ADVANCES) {
                val state = viewModel.uiState.value
                if (state is TriviaUiState.Content && !state.isFinished) {
                    if (!state.isAnswered) viewModel.selectAnswer(state.currentQuestion.correctIndex)
                    viewModel.nextQuestion()
                }
            }

            val finished = viewModel.uiState.value as TriviaUiState.Content
            assertTrue(finished.isFinished)
            assertEquals(finished.totalQuestions, finished.score)
        }

    private companion object {
        const val MAX_ADVANCES = 20
    }
}
