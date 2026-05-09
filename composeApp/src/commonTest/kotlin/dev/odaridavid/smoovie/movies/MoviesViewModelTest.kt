package dev.odaridavid.smoovie.movies

import dev.odaridavid.smoovie.FakeConfigurationRepository
import dev.odaridavid.smoovie.FakeFilterPreferencesStore
import dev.odaridavid.smoovie.FakeMoviesRepository
import dev.odaridavid.smoovie.FakeSettingsPreferencesStore
import dev.odaridavid.smoovie.configuration.ConfigurationRepository
import dev.odaridavid.smoovie.configuration.ConfigurationStore
import dev.odaridavid.smoovie.configuration.LoadConfigurationUseCase
import dev.odaridavid.smoovie.filter.MovieFilterPreferences
import dev.odaridavid.smoovie.filter.MovieSortOption
import dev.odaridavid.smoovie.movies.data.Genre
import dev.odaridavid.smoovie.movies.data.Movie
import dev.odaridavid.smoovie.movies.domain.DiscoverMoviesUseCase
import dev.odaridavid.smoovie.movies.domain.GetGenresUseCase
import dev.odaridavid.smoovie.movies.domain.GetPopularMoviesUseCase
import dev.odaridavid.smoovie.movies.domain.GetTrendingMoviesUseCase
import dev.odaridavid.smoovie.movies.domain.SearchMoviesUseCase
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
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

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
        filterStore: FakeFilterPreferencesStore = FakeFilterPreferencesStore(),
        settingsStore: FakeSettingsPreferencesStore = FakeSettingsPreferencesStore(),
    ) = MoviesViewModel(
        getPopularMovies = GetPopularMoviesUseCase(repo, MovieUiMapper(configStore)),
        getTrendingMovies = GetTrendingMoviesUseCase(repo, MovieUiMapper(configStore)),
        searchMovies = SearchMoviesUseCase(repo, MovieUiMapper(configStore)),
        discoverMovies = DiscoverMoviesUseCase(repo, MovieUiMapper(configStore)),
        getGenres = GetGenresUseCase(repo),
        loadConfiguration = LoadConfigurationUseCase(configRepo, configStore),
        filterPreferencesStore = filterStore,
        settingsPreferencesStore = settingsStore,
    )

    @Test
    fun `given api returns movies - when loadMovies is called - then emits success`() =
        runTest {
            val viewModel = buildViewModel(FakeMoviesRepository(movies = testMovies))

            val state = viewModel.state.value.uiState

            assertIs<MoviesUiState.Success>(state)
            assertEquals(expectedUiModels, state.movies)
        }

    @Test
    fun `given api throws - when loadMovies is called - then emits error`() =
        runTest {
            val viewModel =
                buildViewModel(FakeMoviesRepository(error = Exception("Network error")))

            val state = viewModel.state.value.uiState

            assertIs<MoviesUiState.Error>(state)
            assertEquals(AppError.NetworkError, state.error)
        }

    @Test
    fun `given untyped exception - when loadMovies is called - then emits network error`() =
        runTest {
            val viewModel = buildViewModel(FakeMoviesRepository(error = Exception()))

            val state = viewModel.state.value.uiState

            assertIs<MoviesUiState.Error>(state)
            assertEquals(AppError.NetworkError, state.error)
        }

    @Test
    fun `given error state - when retry is called - then emits success`() =
        runTest {
            val repo = FakeMoviesRepository(error = Exception("Network error"))
            val viewModel = buildViewModel(repo)
            assertIs<MoviesUiState.Error>(viewModel.state.value.uiState)

            repo.error = null
            repo.movies = testMovies
            viewModel.loadMovies()

            val state = viewModel.state.value.uiState
            assertIs<MoviesUiState.Success>(state)
            assertEquals(expectedUiModels, state.movies)
        }

    @Test
    fun `given api returns empty list - when viewmodel is created - then emits empty state`() =
        runTest {
            val viewModel = buildViewModel(FakeMoviesRepository(movies = emptyList()))

            val state = viewModel.state.value.uiState

            assertIs<MoviesUiState.Empty>(state)
        }

    @Test
    fun `given configuration api throws - when viewmodel is created - then emits error`() =
        runTest {
            val viewModel =
                buildViewModel(
                    repo = FakeMoviesRepository(),
                    configRepo = FakeConfigurationRepository(error = Exception("Config error")),
                )

            val state = viewModel.state.value.uiState

            assertIs<MoviesUiState.Error>(state)
            assertEquals(AppError.NetworkError, state.error)
        }

    @Test
    fun `given success with more pages - when loadNextPage is called - then appends movies`() =
        runTest {
            val page2Movies =
                listOf(
                    Movie(id = 3, title = "Tenet", overview = "Time inversion.", voteAverage = 7.8),
                    Movie(id = 4, title = "Oppenheimer", overview = "The bomb.", voteAverage = 8.9),
                )
            val repo = FakeMoviesRepository(movies = testMovies, totalPages = 2)
            val viewModel = buildViewModel(repo)
            val firstPage = viewModel.state.value.uiState
            assertIs<MoviesUiState.Success>(firstPage)
            assertTrue(firstPage.hasMorePages)

            repo.movies = page2Movies
            viewModel.loadNextPage()

            val state = viewModel.state.value.uiState
            assertIs<MoviesUiState.Success>(state)
            assertEquals(testMovies.size + page2Movies.size, state.movies.size)
            assertFalse(state.hasMorePages)
        }

    @Test
    fun `given success on last page - when loadNextPage is called - then movies are not appended`() =
        runTest {
            val repo = FakeMoviesRepository(movies = testMovies, totalPages = 1)
            val viewModel = buildViewModel(repo)
            val initial = viewModel.state.value.uiState
            assertIs<MoviesUiState.Success>(initial)
            assertFalse(initial.hasMorePages)

            viewModel.loadNextPage()

            val state = viewModel.state.value.uiState
            assertIs<MoviesUiState.Success>(state)
            assertEquals(testMovies.size, state.movies.size)
        }

    @Test
    fun `given next page load fails - when loadNextPage - then reverts to previous success state`() =
        runTest {
            val repo = FakeMoviesRepository(movies = testMovies, totalPages = 2)
            val viewModel = buildViewModel(repo)
            assertIs<MoviesUiState.Success>(viewModel.state.value.uiState)

            repo.error = Exception("Network error")
            viewModel.loadNextPage()

            val state = viewModel.state.value.uiState
            assertIs<MoviesUiState.Success>(state)
            assertEquals(testMovies.size, state.movies.size)
            assertFalse(state.isLoadingMore)
        }

    @Test
    fun `given filter applied - when onFilterApplied - then emits discover movies`() =
        runTest {
            val actionMovies = listOf(Movie(id = 10, title = "Mad Max", overview = "Fury Road."))
            val repo = FakeMoviesRepository(movies = testMovies, discoverMovies = actionMovies)
            val viewModel = buildViewModel(repo)

            viewModel.onFilterApplied(28, MovieSortOption.POPULARITY.apiValue, 0f)

            val state = viewModel.state.value.uiState
            assertIs<MoviesUiState.Success>(state)
            assertEquals(1, state.movies.size)
            assertEquals("Mad Max", state.movies[0].title)
        }

    @Test
    fun `given filter applied - when filter reset - then emits popular movies`() =
        runTest {
            val actionMovies = listOf(Movie(id = 10, title = "Mad Max", overview = "Fury Road."))
            val repo = FakeMoviesRepository(movies = testMovies, discoverMovies = actionMovies)
            val viewModel = buildViewModel(repo)
            viewModel.onFilterApplied(28, MovieSortOption.POPULARITY.apiValue, 0f)
            assertIs<MoviesUiState.Success>(viewModel.state.value.uiState)

            viewModel.onFilterApplied(null, MovieSortOption.POPULARITY.apiValue, 0f)

            val state = viewModel.state.value.uiState
            assertIs<MoviesUiState.Success>(state)
            assertEquals(expectedUiModels, state.movies)
        }

    @Test
    fun `given filter applied - when filter saved - then filter preferences are persisted`() =
        runTest {
            val repo = FakeMoviesRepository(movies = testMovies)
            val filterStore = FakeFilterPreferencesStore()
            val viewModel = buildViewModel(repo, filterStore = filterStore)
            viewModel.onFilterApplied(28, MovieSortOption.RATING.apiValue, 0f)

            assertEquals(MovieFilterPreferences(selectedGenreId = 28, sortBy = MovieSortOption.RATING), filterStore.movieFilter)
        }

    @Test
    fun `given saved filter in store - when viewmodel is created - then filter preferences are restored`() =
        runTest {
            val actionMovies = listOf(Movie(id = 10, title = "Mad Max", overview = "Fury Road."))
            val repo = FakeMoviesRepository(movies = testMovies, discoverMovies = actionMovies)
            val savedFilter = MovieFilterPreferences(selectedGenreId = 28)
            val filterStore = FakeFilterPreferencesStore(movieFilter = savedFilter)

            val viewModel = buildViewModel(repo, filterStore = filterStore)

            assertEquals(savedFilter, viewModel.state.value.filterPreferences)
        }

    @Test
    fun `given filter applied with more pages - when loadNextPage - then appends discover movies`() =
        runTest {
            val page1Movies = listOf(Movie(id = 10, title = "Mad Max", overview = "Fury Road."))
            val page2Movies = listOf(Movie(id = 11, title = "Die Hard", overview = "Action film."))
            val repo = FakeMoviesRepository(discoverMovies = page1Movies, totalPages = 2)
            val viewModel = buildViewModel(repo)
            viewModel.onFilterApplied(28, MovieSortOption.POPULARITY.apiValue, 0f)
            assertIs<MoviesUiState.Success>(viewModel.state.value.uiState)
            assertTrue((viewModel.state.value.uiState as MoviesUiState.Success).hasMorePages)

            repo.discoverMovies = page2Movies
            viewModel.loadNextPage()

            val state = viewModel.state.value.uiState
            assertIs<MoviesUiState.Success>(state)
            assertEquals(2, state.movies.size)
            assertFalse(state.hasMorePages)
        }

    @Test
    fun `given genres available - when viewmodel is created - then genres state is populated`() =
        runTest {
            val genres = listOf(Genre(28, "Action"), Genre(35, "Comedy"))
            val repo = FakeMoviesRepository(movies = testMovies, genres = genres)
            val viewModel = buildViewModel(repo)

            assertEquals(2, viewModel.state.value.genres.size)
            assertEquals(
                "Action",
                viewModel.state.value.genres[0]
                    .name,
            )
            assertEquals(
                "Comedy",
                viewModel.state.value.genres[1]
                    .name,
            )
        }

    @Test
    fun `given genres api throws - when viewmodel is created - then movies still load`() =
        runTest {
            val repo = FakeMoviesRepository(movies = testMovies, genresError = Exception("genres failed"))
            val viewModel = buildViewModel(repo)

            assertIs<MoviesUiState.Success>(viewModel.state.value.uiState)
            assertEquals(emptyList(), viewModel.state.value.genres)
        }
}
