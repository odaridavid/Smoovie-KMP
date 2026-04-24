package dev.odaridavid.smoovie.shows

import dev.odaridavid.smoovie.FakeTvShowsRepository
import dev.odaridavid.smoovie.configuration.ConfigurationStore
import dev.odaridavid.smoovie.shows.data.Season
import dev.odaridavid.smoovie.shows.data.TvGenre
import dev.odaridavid.smoovie.shows.data.TvShowDetail
import dev.odaridavid.smoovie.shows.domain.GetTvShowDetailUseCase
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
class TvShowDetailViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    private val detail =
        TvShowDetail(
            id = 1396,
            name = "Breaking Bad",
            overview = "Chemistry teacher.",
            tagline = "Remember my name.",
            firstAirDate = "2008-01-20",
            lastAirDate = "2013-09-29",
            voteAverage = 9.5,
            voteCount = 15_000,
            inProduction = false,
            numberOfSeasons = 5,
            numberOfEpisodes = 62,
            genres = listOf(TvGenre(18, "Drama"), TvGenre(80, "Crime")),
            seasons =
                listOf(
                    Season(id = 1, name = "Season 1", seasonNumber = 1, episodeCount = 7, airDate = "2008-01-20"),
                    Season(id = 2, name = "Season 2", seasonNumber = 2, episodeCount = 13, airDate = "2009-03-08"),
                    Season(id = 99, name = "Specials", seasonNumber = 0, episodeCount = 3, airDate = null),
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
        repo: FakeTvShowsRepository,
        configStore: ConfigurationStore = ConfigurationStore(),
    ) = TvShowDetailViewModel(
        tvShowId = detail.id,
        presentLabel = "present",
        getTvShowDetail = GetTvShowDetailUseCase(repo, configStore),
    )

    @Test
    fun `given api returns detail - when viewmodel is created - then emits success`() =
        runTest {
            val repo = FakeTvShowsRepository(tvShowDetail = detail)
            val viewModel = buildViewModel(repo)

            val state = viewModel.uiState.value

            assertIs<TvShowDetailUiState.Success>(state)
            assertEquals("Breaking Bad", state.tvShowDetail.name)
            assertEquals("5 seasons · 62 episodes", state.tvShowDetail.seasonsLabel)
            assertEquals("2008 – 2013", state.tvShowDetail.yearsRange)
            assertEquals("Drama, Crime", state.tvShowDetail.genres)
        }

    @Test
    fun `given in-production show - when loaded - then years range ends in present label`() =
        runTest {
            val repo = FakeTvShowsRepository(tvShowDetail = detail.copy(inProduction = true, lastAirDate = ""))
            val viewModel = buildViewModel(repo)

            val state = viewModel.uiState.value as TvShowDetailUiState.Success
            assertEquals("2008 – present", state.tvShowDetail.yearsRange)
        }

    @Test
    fun `given detail has specials - when mapped - then specials are filtered out`() =
        runTest {
            val repo = FakeTvShowsRepository(tvShowDetail = detail)
            val viewModel = buildViewModel(repo)

            val state = viewModel.uiState.value as TvShowDetailUiState.Success
            val seasonNumbers = state.tvShowDetail.seasons.map { it.name }
            assertEquals(listOf("Season 1", "Season 2"), seasonNumbers)
        }

    @Test
    fun `given single season show - when loaded - then label uses singular`() =
        runTest {
            val oneSeason = detail.copy(numberOfSeasons = 1, numberOfEpisodes = 1)
            val repo = FakeTvShowsRepository(tvShowDetail = oneSeason)
            val viewModel = buildViewModel(repo)

            val state = viewModel.uiState.value as TvShowDetailUiState.Success
            assertEquals("1 season · 1 episode", state.tvShowDetail.seasonsLabel)
        }

    @Test
    fun `given api throws - when viewmodel is created - then emits network error`() =
        runTest {
            val repo = FakeTvShowsRepository(error = Exception("Network"))
            val viewModel = buildViewModel(repo)

            val state = viewModel.uiState.value
            assertIs<TvShowDetailUiState.Error>(state)
            assertEquals(AppError.NetworkError, state.error)
        }

    @Test
    fun `given error state - when retry is called - then emits success`() =
        runTest {
            val repo = FakeTvShowsRepository(error = Exception("Network"))
            val viewModel = buildViewModel(repo)
            assertIs<TvShowDetailUiState.Error>(viewModel.uiState.value)

            repo.error = null
            repo.tvShowDetail = detail
            viewModel.loadTvShowDetail()

            val state = viewModel.uiState.value
            assertIs<TvShowDetailUiState.Success>(state)
            assertEquals("Breaking Bad", state.tvShowDetail.name)
        }
}
