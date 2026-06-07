package dev.odaridavid.smoovie.trivia

import dev.odaridavid.smoovie.FakeMoviesRepository
import dev.odaridavid.smoovie.movies.data.Genre
import dev.odaridavid.smoovie.movies.data.MovieDetail
import dev.odaridavid.smoovie.trivia.domain.GenerateMovieTriviaUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GenerateMovieTriviaUseCaseTest {
    private val movieGenres = listOf(Genre(id = 18, name = "Drama"), Genre(id = 878, name = "Science Fiction"))

    private fun buildRepository(detail: MovieDetail) =
        FakeMoviesRepository(
            movieDetail = detail,
            genres =
                movieGenres +
                    listOf(
                        Genre(id = 35, name = "Comedy"),
                        Genre(id = 27, name = "Horror"),
                        Genre(id = 37, name = "Western"),
                        Genre(id = 10749, name = "Romance"),
                    ),
        )

    @Test
    fun `given a movie with full detail - when generating trivia - then questions are produced`() =
        runTest {
            val detail =
                MovieDetail(
                    id = 1,
                    title = "Interstellar",
                    overview = "",
                    releaseDate = "2014-11-05",
                    runtime = 169,
                    genres = movieGenres,
                )
            val useCase = GenerateMovieTriviaUseCase(buildRepository(detail))

            val questions = useCase(movieId = 1)

            assertTrue(questions.isNotEmpty())
            questions.forEach { question ->
                assertTrue(question.options.size >= 2, "expected at least 2 options")
                assertTrue(question.correctIndex in question.options.indices, "correctIndex out of bounds")
            }
        }

    @Test
    fun `given a movie release year - when generating trivia - then the year question marks the right answer`() =
        runTest {
            val detail =
                MovieDetail(
                    id = 1,
                    title = "Interstellar",
                    overview = "",
                    releaseDate = "2014-11-05",
                    runtime = 169,
                    genres = movieGenres,
                )
            val useCase = GenerateMovieTriviaUseCase(buildRepository(detail))

            val questions = useCase(movieId = 1)
            val yearQuestion = questions.first { it.kind == TriviaQuestionKind.RELEASE_YEAR }

            assertEquals("2014", yearQuestion.options[yearQuestion.correctIndex])
        }

    @Test
    fun `given a movie with no usable data - when generating trivia - then no questions are produced`() =
        runTest {
            val detail =
                MovieDetail(
                    id = 1,
                    title = "Mystery",
                    overview = "",
                    releaseDate = "",
                    runtime = null,
                    genres = emptyList(),
                )
            val useCase =
                GenerateMovieTriviaUseCase(
                    FakeMoviesRepository(movieDetail = detail, genres = emptyList()),
                )

            val questions = useCase(movieId = 1)

            assertTrue(questions.isEmpty())
        }
}
