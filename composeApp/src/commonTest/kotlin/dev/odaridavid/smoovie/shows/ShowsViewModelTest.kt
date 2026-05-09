package dev.odaridavid.smoovie.shows

import dev.odaridavid.smoovie.FakeConfigurationRepository
import dev.odaridavid.smoovie.FakeFilterPreferencesStore
import dev.odaridavid.smoovie.FakeSettingsPreferencesStore
import dev.odaridavid.smoovie.FakeTvShowsRepository
import dev.odaridavid.smoovie.configuration.ConfigurationRepository
import dev.odaridavid.smoovie.configuration.ConfigurationStore
import dev.odaridavid.smoovie.configuration.LoadConfigurationUseCase
import dev.odaridavid.smoovie.filter.TvFilterPreferences
import dev.odaridavid.smoovie.filter.TvSortOption
import dev.odaridavid.smoovie.shows.data.TvGenre
import dev.odaridavid.smoovie.shows.data.TvShow
import dev.odaridavid.smoovie.shows.domain.DiscoverTvShowsUseCase
import dev.odaridavid.smoovie.shows.domain.GetPopularTvShowsUseCase
import dev.odaridavid.smoovie.shows.domain.GetTvGenresUseCase
import dev.odaridavid.smoovie.shows.domain.SearchTvShowsUseCase
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
class ShowsViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    private val testShows =
        listOf(
            TvShow(id = 1, name = "Breaking Bad", overview = "Chemistry."),
            TvShow(id = 2, name = "Stranger Things", overview = "Upside down."),
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
        repo: FakeTvShowsRepository,
        configRepo: ConfigurationRepository = FakeConfigurationRepository(),
        configStore: ConfigurationStore = ConfigurationStore(),
        filterStore: FakeFilterPreferencesStore = FakeFilterPreferencesStore(),
        settingsStore: FakeSettingsPreferencesStore = FakeSettingsPreferencesStore(),
    ): ShowsViewModel {
        val mapper = TvShowUiMapper(configStore)
        return ShowsViewModel(
            getPopularTvShows = GetPopularTvShowsUseCase(repo, mapper),
            searchTvShows = SearchTvShowsUseCase(repo, mapper),
            discoverTvShows = DiscoverTvShowsUseCase(repo, mapper),
            getTvGenres = GetTvGenresUseCase(repo),
            loadConfiguration = LoadConfigurationUseCase(configRepo, configStore),
            filterPreferencesStore = filterStore,
            settingsPreferencesStore = settingsStore,
        )
    }

    @Test
    fun `given api returns shows - when viewmodel is created - then emits success`() =
        runTest {
            val viewModel = buildViewModel(FakeTvShowsRepository(tvShows = testShows))

            val state = viewModel.state.value.uiState

            assertIs<ShowsUiState.Success>(state)
            assertEquals(2, state.tvShows.size)
            assertEquals("Breaking Bad", state.tvShows[0].name)
        }

    @Test
    fun `given api throws - when viewmodel is created - then emits network error`() =
        runTest {
            val viewModel = buildViewModel(FakeTvShowsRepository(error = Exception("Network")))

            val state = viewModel.state.value.uiState

            assertIs<ShowsUiState.Error>(state)
            assertEquals(AppError.NetworkError, state.error)
        }

    @Test
    fun `given api returns empty - when viewmodel is created - then emits empty state`() =
        runTest {
            val viewModel = buildViewModel(FakeTvShowsRepository(tvShows = emptyList()))

            assertIs<ShowsUiState.Empty>(viewModel.state.value.uiState)
        }

    @Test
    fun `given error - when retry is called - then emits success`() =
        runTest {
            val repo = FakeTvShowsRepository(error = Exception("Network"))
            val viewModel = buildViewModel(repo)
            assertIs<ShowsUiState.Error>(viewModel.state.value.uiState)

            repo.error = null
            repo.tvShows = testShows
            viewModel.retry()

            assertIs<ShowsUiState.Success>(viewModel.state.value.uiState)
        }

    @Test
    fun `given filter applied - when onFilterApplied - then emits discover shows`() =
        runTest {
            val dramaShows = listOf(TvShow(id = 10, name = "The Wire", overview = "Baltimore."))
            val repo = FakeTvShowsRepository(tvShows = testShows, discoverTvShows = dramaShows)
            val viewModel = buildViewModel(repo)

            viewModel.onFilterApplied(18, TvSortOption.POPULARITY.apiValue, 0f)

            val state = viewModel.state.value.uiState
            assertIs<ShowsUiState.Success>(state)
            assertEquals(1, state.tvShows.size)
            assertEquals("The Wire", state.tvShows[0].name)
        }

    @Test
    fun `given filter applied - when filter reset - then emits popular shows`() =
        runTest {
            val dramaShows = listOf(TvShow(id = 10, name = "The Wire", overview = "Baltimore."))
            val repo = FakeTvShowsRepository(tvShows = testShows, discoverTvShows = dramaShows)
            val viewModel = buildViewModel(repo)
            viewModel.onFilterApplied(18, TvSortOption.POPULARITY.apiValue, 0f)
            assertIs<ShowsUiState.Success>(viewModel.state.value.uiState)

            viewModel.onFilterApplied(null, TvSortOption.POPULARITY.apiValue, 0f)

            val state = viewModel.state.value.uiState
            assertIs<ShowsUiState.Success>(state)
            assertEquals(testShows.size, state.tvShows.size)
        }

    @Test
    fun `given filter applied - when filter saved - then filter preferences are persisted`() =
        runTest {
            val repo = FakeTvShowsRepository(tvShows = testShows)
            val filterStore = FakeFilterPreferencesStore()
            val viewModel = buildViewModel(repo, filterStore = filterStore)
            viewModel.onFilterApplied(18, TvSortOption.RATING.apiValue, 0f)

            assertEquals(TvFilterPreferences(selectedGenreId = 18, sortBy = TvSortOption.RATING), filterStore.tvFilter)
        }

    @Test
    fun `given saved filter in store - when viewmodel is created - then filter preferences are restored`() =
        runTest {
            val dramaShows = listOf(TvShow(id = 10, name = "The Wire", overview = "Baltimore."))
            val repo = FakeTvShowsRepository(tvShows = testShows, discoverTvShows = dramaShows)
            val savedFilter = TvFilterPreferences(selectedGenreId = 18)
            val filterStore = FakeFilterPreferencesStore(tvFilter = savedFilter)

            val viewModel = buildViewModel(repo, filterStore = filterStore)

            assertEquals(savedFilter, viewModel.state.value.filterPreferences)
        }

    @Test
    fun `given genres api throws - when viewmodel is created - then shows still load`() =
        runTest {
            val repo = FakeTvShowsRepository(tvShows = testShows, genresError = Exception("genres failed"))
            val viewModel = buildViewModel(repo)

            assertIs<ShowsUiState.Success>(viewModel.state.value.uiState)
            assertEquals(emptyList(), viewModel.state.value.genres)
        }

    @Test
    fun `given genres available - when viewmodel is created - then genres state is populated`() =
        runTest {
            val genres = listOf(TvGenre(18, "Drama"), TvGenre(35, "Comedy"))
            val repo = FakeTvShowsRepository(tvShows = testShows, genres = genres)
            val viewModel = buildViewModel(repo)

            assertEquals(2, viewModel.state.value.genres.size)
            assertEquals(
                "Drama",
                viewModel.state.value.genres[0]
                    .name,
            )
        }

    @Test
    fun `given initial load - when viewmodel is created - then featured shows are populated`() =
        runTest {
            val viewModel = buildViewModel(FakeTvShowsRepository(tvShows = testShows))

            assertEquals(testShows.size, viewModel.state.value.featuredTvShows.size)
            assertEquals(
                testShows[0].id,
                viewModel.state.value.featuredTvShows[0]
                    .id,
            )
        }

    @Test
    fun `given featured populated - when filter applied - then featured stays`() =
        runTest {
            val dramaShows = listOf(TvShow(id = 10, name = "The Wire", overview = "Baltimore."))
            val repo = FakeTvShowsRepository(tvShows = testShows, discoverTvShows = dramaShows)
            val viewModel = buildViewModel(repo)
            val featuredBefore = viewModel.state.value.featuredTvShows

            viewModel.onFilterApplied(18, TvSortOption.POPULARITY.apiValue, 0f)

            assertEquals(featuredBefore, viewModel.state.value.featuredTvShows)
        }

    @Test
    fun `given success with more pages - when loadNextPage - then appends shows`() =
        runTest {
            val page2Shows =
                listOf(
                    TvShow(id = 3, name = "Succession", overview = "Media dynasty."),
                )
            val repo = FakeTvShowsRepository(tvShows = testShows, totalPages = 2)
            val viewModel = buildViewModel(repo)
            val firstPage = viewModel.state.value.uiState
            assertIs<ShowsUiState.Success>(firstPage)
            assertTrue(firstPage.hasMorePages)

            repo.tvShows = page2Shows
            viewModel.loadNextPage()

            val state = viewModel.state.value.uiState
            assertIs<ShowsUiState.Success>(state)
            assertEquals(testShows.size + page2Shows.size, state.tvShows.size)
            assertFalse(state.hasMorePages)
        }
}
