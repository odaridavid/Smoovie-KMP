package dev.odaridavid.smoovie.movies.domain

import dev.odaridavid.smoovie.FakeMoviesRepository
import dev.odaridavid.smoovie.configuration.ConfigurationStore
import dev.odaridavid.smoovie.filter.MovieFilterPreferences
import dev.odaridavid.smoovie.filter.MovieSortOption
import dev.odaridavid.smoovie.movies.MovieUiMapper
import dev.odaridavid.smoovie.movies.data.Movie
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DiscoverMoviesUseCaseTest {
    private val store = ConfigurationStore()
    private val mapper = MovieUiMapper(store)

    private fun buildUseCase(repo: FakeMoviesRepository) = DiscoverMoviesUseCase(repo, mapper)

    @Test
    fun `given default filter - when invoked - then returns mapped discover movies`() =
        runTest {
            val movies =
                listOf(
                    Movie(id = 1, title = "Mad Max", overview = "Fury Road."),
                )
            val useCase = buildUseCase(FakeMoviesRepository(discoverMovies = movies))

            val result = useCase(filter = MovieFilterPreferences())

            assertEquals(1, result.movies.size)
            assertEquals("Mad Max", result.movies.first().title)
        }

    @Test
    fun `given genre filter - when invoked - then passes genre id to repository`() =
        runTest {
            val actionMovies = listOf(Movie(id = 10, title = "Action Hero", overview = ""))
            val repo = FakeMoviesRepository(discoverMovies = actionMovies)
            val useCase = buildUseCase(repo)

            val result = useCase(filter = MovieFilterPreferences(selectedGenreId = 28))

            assertEquals(1, result.movies.size)
            assertEquals("Action Hero", result.movies.first().title)
        }

    @Test
    fun `given rating sort - when invoked - then returns movies from discover endpoint`() =
        runTest {
            val movies =
                listOf(
                    Movie(id = 1, title = "High Rated", overview = "", voteAverage = 9.0),
                )
            val useCase = buildUseCase(FakeMoviesRepository(discoverMovies = movies))

            val result = useCase(filter = MovieFilterPreferences(sortBy = MovieSortOption.RATING))

            assertEquals("High Rated", result.movies.first().title)
        }

    @Test
    fun `given repository returns empty - when invoked - then returns empty movies`() =
        runTest {
            val useCase = buildUseCase(FakeMoviesRepository(discoverMovies = emptyList()))

            val result = useCase(filter = MovieFilterPreferences())

            assertTrue(result.movies.isEmpty())
        }

    @Test
    fun `given repository throws - when invoked - then propagates exception`() =
        runTest {
            val useCase = buildUseCase(FakeMoviesRepository(error = Exception("Network error")))

            runCatching { useCase(MovieFilterPreferences()) }.also { assertTrue(it.isFailure) }
        }

    @Test
    fun `given multiple pages - when invoked with page 2 - then returns correct page info`() =
        runTest {
            val movies = listOf(Movie(id = 2, title = "Page 2", overview = ""))
            val useCase = buildUseCase(FakeMoviesRepository(discoverMovies = movies, totalPages = 3))

            val result = useCase(filter = MovieFilterPreferences(), page = 2)

            assertEquals(2, result.page)
            assertEquals(3, result.totalPages)
        }
}
