package dev.odaridavid.smoovie.watchlist

import dev.odaridavid.smoovie.FakeWatchlistRepository
import dev.odaridavid.smoovie.movies.MovieUiModel
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
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class WatchlistViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    private val entry =
        WatchlistEntry(
            id = 1,
            title = "Interstellar",
            overview = "Space.",
            releaseDate = "2014",
            voteAverage = "8.6",
            backdropUrl = null,
            posterUrl = null,
        )

    private val movie =
        MovieUiModel(
            id = entry.id,
            title = entry.title,
            overview = entry.overview,
            releaseDate = entry.releaseDate,
            voteAverage = entry.voteAverage,
            backdropUrl = null,
            posterUrl = null,
        )

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel(repo: FakeWatchlistRepository = FakeWatchlistRepository()): WatchlistViewModel =
        WatchlistViewModel(
            observeWatchlist = ObserveWatchlistUseCase(repo),
            removeFromWatchlist = RemoveFromWatchlistUseCase(repo),
        )

    @Test
    fun `given empty repo - when observed - then state is empty`() =
        runTest {
            val viewModel = buildViewModel()
            backgroundScope.launch(testDispatcher) { viewModel.state.collect {} }

            assertEquals(WatchlistUiState.Empty, viewModel.state.value)
        }

    @Test
    fun `given repo with entries - when observed - then state is loaded with mapped movies`() =
        runTest {
            val repo = FakeWatchlistRepository()
            repo.toggle(entry)
            val viewModel = buildViewModel(repo)
            backgroundScope.launch(testDispatcher) { viewModel.state.collect {} }

            val state = viewModel.state.value
            assertIs<WatchlistUiState.Loaded>(state)
            assertEquals(1, state.movies.size)
            assertEquals(entry.id, state.movies[0].id)
            assertEquals(entry.title, state.movies[0].title)
        }

    @Test
    fun `given loaded - when repo removes entry - then state falls back to empty`() =
        runTest {
            val repo = FakeWatchlistRepository()
            repo.toggle(entry)
            val viewModel = buildViewModel(repo)
            backgroundScope.launch(testDispatcher) { viewModel.state.collect {} }
            assertIs<WatchlistUiState.Loaded>(viewModel.state.value)

            repo.remove(entry.id)

            assertEquals(WatchlistUiState.Empty, viewModel.state.value)
        }

    @Test
    fun `given loaded - when remove is called - then entry is removed from repo`() =
        runTest {
            val repo = FakeWatchlistRepository()
            repo.toggle(entry)
            val viewModel = buildViewModel(repo)
            backgroundScope.launch(testDispatcher) { viewModel.state.collect {} }
            assertIs<WatchlistUiState.Loaded>(viewModel.state.value)

            viewModel.remove(movie)

            assertEquals(WatchlistUiState.Empty, viewModel.state.value)
        }

    @Test
    fun `given empty - when repo toggles new entry - then state becomes loaded`() =
        runTest {
            val repo = FakeWatchlistRepository()
            val viewModel = buildViewModel(repo)
            backgroundScope.launch(testDispatcher) { viewModel.state.collect {} }
            assertEquals(WatchlistUiState.Empty, viewModel.state.value)

            repo.toggle(entry)

            val state = viewModel.state.value
            assertIs<WatchlistUiState.Loaded>(state)
            assertEquals(entry.id, state.movies.single().id)
        }
}
