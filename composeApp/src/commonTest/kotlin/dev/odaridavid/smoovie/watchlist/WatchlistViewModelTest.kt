package dev.odaridavid.smoovie.watchlist

import dev.odaridavid.smoovie.FakeAppReviewRequester
import dev.odaridavid.smoovie.FakeWatchlistRepository
import dev.odaridavid.smoovie.watchlist.domain.MediaType
import dev.odaridavid.smoovie.watchlist.domain.ObserveWatchlistUseCase
import dev.odaridavid.smoovie.watchlist.domain.RemoveFromWatchlistUseCase
import dev.odaridavid.smoovie.watchlist.domain.WatchlistEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
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
class WatchlistViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    private val movieEntry =
        WatchlistEntry(
            id = 1,
            title = "Interstellar",
            overview = "Space.",
            releaseDate = "2014",
            voteAverage = "8.6",
            backdropUrl = null,
            posterUrl = null,
            mediaType = MediaType.MOVIE,
        )

    private val tvEntry =
        WatchlistEntry(
            id = 2,
            title = "Breaking Bad",
            overview = "Chemistry.",
            releaseDate = "2008",
            voteAverage = "9.5",
            backdropUrl = null,
            posterUrl = null,
            mediaType = MediaType.TV,
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
        repo: FakeWatchlistRepository = FakeWatchlistRepository(),
        appReviewRequester: FakeAppReviewRequester = FakeAppReviewRequester(),
    ): WatchlistViewModel =
        WatchlistViewModel(
            observeWatchlist = ObserveWatchlistUseCase(repo),
            removeFromWatchlist = RemoveFromWatchlistUseCase(repo),
            appReviewRequester = appReviewRequester,
        )

    @Test
    fun `given empty repo - when observed - then state is empty`() =
        runTest {
            val viewModel = buildViewModel()
            backgroundScope.launch(testDispatcher) { viewModel.state.collect {} }

            assertEquals(WatchlistUiState.Empty, viewModel.state.value)
        }

    @Test
    fun `given repo with mixed entries - when observed - then state is loaded with mapped items`() =
        runTest {
            val repo = FakeWatchlistRepository()
            repo.toggle(movieEntry)
            repo.toggle(tvEntry)
            val viewModel = buildViewModel(repo)
            backgroundScope.launch(testDispatcher) { viewModel.state.collect {} }

            val state = viewModel.state.value
            assertIs<WatchlistUiState.Loaded>(state)
            assertEquals(WatchlistFilter.ALL, state.filter)
            assertEquals(2, state.items.size)
            assertTrue(state.items.any { it is WatchlistItemUiModel.Movie && it.movie.id == movieEntry.id })
            assertTrue(state.items.any { it is WatchlistItemUiModel.TvShow && it.tvShow.id == tvEntry.id })
        }

    @Test
    fun `given mixed entries - when filter set to movies - then only movie items remain`() =
        runTest {
            val repo = FakeWatchlistRepository()
            repo.toggle(movieEntry)
            repo.toggle(tvEntry)
            val viewModel = buildViewModel(repo)
            backgroundScope.launch(testDispatcher) { viewModel.state.collect {} }

            viewModel.setFilter(WatchlistFilter.MOVIES)

            val state = viewModel.state.value
            assertIs<WatchlistUiState.Loaded>(state)
            assertEquals(WatchlistFilter.MOVIES, state.filter)
            assertEquals(1, state.items.size)
            assertIs<WatchlistItemUiModel.Movie>(state.items.single())
        }

    @Test
    fun `given mixed entries - when filter set to tv - then only tv items remain`() =
        runTest {
            val repo = FakeWatchlistRepository()
            repo.toggle(movieEntry)
            repo.toggle(tvEntry)
            val viewModel = buildViewModel(repo)
            backgroundScope.launch(testDispatcher) { viewModel.state.collect {} }

            viewModel.setFilter(WatchlistFilter.TV_SHOWS)

            val state = viewModel.state.value
            assertIs<WatchlistUiState.Loaded>(state)
            assertEquals(1, state.items.size)
            assertIs<WatchlistItemUiModel.TvShow>(state.items.single())
        }

    @Test
    fun `given only movies - when filter set to tv - then loaded with empty list`() =
        runTest {
            val repo = FakeWatchlistRepository()
            repo.toggle(movieEntry)
            val viewModel = buildViewModel(repo)
            backgroundScope.launch(testDispatcher) { viewModel.state.collect {} }

            viewModel.setFilter(WatchlistFilter.TV_SHOWS)

            val state = viewModel.state.value
            assertIs<WatchlistUiState.Loaded>(state)
            assertEquals(WatchlistFilter.TV_SHOWS, state.filter)
            assertTrue(state.items.isEmpty())
        }

    @Test
    fun `given loaded - when repo removes entry - then state falls back to empty`() =
        runTest {
            val repo = FakeWatchlistRepository()
            repo.toggle(movieEntry)
            val viewModel = buildViewModel(repo)
            backgroundScope.launch(testDispatcher) { viewModel.state.collect {} }
            assertIs<WatchlistUiState.Loaded>(viewModel.state.value)

            repo.remove(movieEntry.id, movieEntry.mediaType)

            assertEquals(WatchlistUiState.Empty, viewModel.state.value)
        }

    @Test
    fun `given loaded movie - when remove called on movie item - then movie is removed from repo`() =
        runTest {
            val repo = FakeWatchlistRepository()
            repo.toggle(movieEntry)
            val viewModel = buildViewModel(repo)
            backgroundScope.launch(testDispatcher) { viewModel.state.collect {} }
            val state = viewModel.state.value as WatchlistUiState.Loaded
            val movieItem = state.items.single()

            viewModel.remove(movieItem)

            assertEquals(WatchlistUiState.Empty, viewModel.state.value)
        }

    @Test
    fun `given loaded tv show - when remove called on tv item - then tv show is removed from repo`() =
        runTest {
            val repo = FakeWatchlistRepository()
            repo.toggle(tvEntry)
            val viewModel = buildViewModel(repo)
            backgroundScope.launch(testDispatcher) { viewModel.state.collect {} }
            val tvItem = (viewModel.state.value as WatchlistUiState.Loaded).items.single()

            viewModel.remove(tvItem)

            assertEquals(WatchlistUiState.Empty, viewModel.state.value)
        }

    @Test
    fun `given empty - when repo toggles new entry - then state becomes loaded`() =
        runTest {
            val repo = FakeWatchlistRepository()
            val viewModel = buildViewModel(repo)
            backgroundScope.launch(testDispatcher) { viewModel.state.collect {} }
            assertEquals(WatchlistUiState.Empty, viewModel.state.value)

            repo.toggle(movieEntry)

            val state = viewModel.state.value
            assertIs<WatchlistUiState.Loaded>(state)
            assertEquals(movieEntry.id, (state.items.single() as WatchlistItemUiModel.Movie).movie.id)
        }

    @Test
    fun `given watchlist reaches 3 items - when observed - then review is requested`() =
        runTest {
            val repo = FakeWatchlistRepository()
            val reviewer = FakeAppReviewRequester()
            val viewModel = buildViewModel(repo, reviewer)
            backgroundScope.launch(testDispatcher) { viewModel.state.collect {} }

            repo.toggle(movieEntry)
            repo.toggle(tvEntry)
            repo.toggle(movieEntry.copy(id = 3, title = "Third"))

            assertEquals(1, reviewer.requestCount)
        }

    @Test
    fun `given watchlist below threshold - when observed - then review is not requested`() =
        runTest {
            val repo = FakeWatchlistRepository()
            val reviewer = FakeAppReviewRequester()
            val viewModel = buildViewModel(repo, reviewer)
            backgroundScope.launch(testDispatcher) { viewModel.state.collect {} }

            repo.toggle(movieEntry)
            repo.toggle(tvEntry)

            assertFalse(reviewer.requestCount > 0)
        }
}
