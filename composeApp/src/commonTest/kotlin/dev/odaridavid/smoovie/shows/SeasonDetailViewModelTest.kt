package dev.odaridavid.smoovie.shows

import dev.odaridavid.smoovie.FakeTvShowsRepository
import dev.odaridavid.smoovie.configuration.ConfigurationStore
import dev.odaridavid.smoovie.shows.data.Episode
import dev.odaridavid.smoovie.shows.data.SeasonDetail
import dev.odaridavid.smoovie.shows.domain.GetSeasonDetailUseCase
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
class SeasonDetailViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    private val seasonDetail =
        SeasonDetail(
            id = 3572,
            name = "Season 1",
            overview = "Walter's diagnosis leads to a fateful decision.",
            seasonNumber = 1,
            airDate = "2008-01-20",
            episodes =
                listOf(
                    Episode(
                        id = 2,
                        name = "Cat's in the Bag...",
                        overview = "Aftermath.",
                        seasonNumber = 1,
                        episodeNumber = 2,
                        airDate = "2008-01-27",
                        runtime = 48,
                        voteAverage = 8.2,
                    ),
                    Episode(
                        id = 1,
                        name = "Pilot",
                        overview = "Chemistry teacher's diagnosis.",
                        seasonNumber = 1,
                        episodeNumber = 1,
                        airDate = "2008-01-20",
                        runtime = 58,
                        voteAverage = 8.3,
                    ),
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
    ) = SeasonDetailViewModel(
        tvShowId = 1396,
        seasonNumber = 1,
        getSeasonDetail = GetSeasonDetailUseCase(repo, configStore),
    )

    @Test
    fun `given api returns season - when viewmodel is created - then emits success`() =
        runTest {
            val viewModel = buildViewModel(FakeTvShowsRepository(seasonDetail = seasonDetail))

            val state = viewModel.uiState.value
            assertIs<SeasonDetailUiState.Success>(state)
            assertEquals("Season 1", state.seasonDetail.name)
            assertEquals("2008", state.seasonDetail.year)
            assertEquals("2 episodes", state.seasonDetail.episodeCountLabel)
        }

    @Test
    fun `given episodes out of order - when mapped - then sorted by episode number`() =
        runTest {
            val viewModel = buildViewModel(FakeTvShowsRepository(seasonDetail = seasonDetail))

            val state = viewModel.uiState.value as SeasonDetailUiState.Success
            assertEquals(listOf(1, 2), state.seasonDetail.episodes.map { it.episodeNumber })
        }

    @Test
    fun `given episode with name - when mapped - then header label combines number and name`() =
        runTest {
            val viewModel = buildViewModel(FakeTvShowsRepository(seasonDetail = seasonDetail))

            val state = viewModel.uiState.value as SeasonDetailUiState.Success
            assertEquals("Ep 1 · Pilot", state.seasonDetail.episodes[0].headerLabel)
        }

    @Test
    fun `given episode without name - when mapped - then header label is just number`() =
        runTest {
            val withoutName = seasonDetail.copy(episodes = listOf(seasonDetail.episodes[0].copy(name = "")))
            val viewModel = buildViewModel(FakeTvShowsRepository(seasonDetail = withoutName))

            val state = viewModel.uiState.value as SeasonDetailUiState.Success
            assertEquals("Ep 2", state.seasonDetail.episodes[0].headerLabel)
        }

    @Test
    fun `given single episode - when mapped - then episode count label is singular`() =
        runTest {
            val oneEpisode = seasonDetail.copy(episodes = listOf(seasonDetail.episodes[0]))
            val viewModel = buildViewModel(FakeTvShowsRepository(seasonDetail = oneEpisode))

            val state = viewModel.uiState.value as SeasonDetailUiState.Success
            assertEquals("1 episode", state.seasonDetail.episodeCountLabel)
        }

    @Test
    fun `given episode with runtime - when mapped - then runtime label shows minutes`() =
        runTest {
            val viewModel = buildViewModel(FakeTvShowsRepository(seasonDetail = seasonDetail))

            val state = viewModel.uiState.value as SeasonDetailUiState.Success
            assertEquals("58 min", state.seasonDetail.episodes[0].runtimeLabel)
        }

    @Test
    fun `given episode without runtime - when mapped - then runtime label is blank`() =
        runTest {
            val withoutRuntime = seasonDetail.copy(episodes = listOf(seasonDetail.episodes[0].copy(runtime = null)))
            val viewModel = buildViewModel(FakeTvShowsRepository(seasonDetail = withoutRuntime))

            val state = viewModel.uiState.value as SeasonDetailUiState.Success
            assertEquals("", state.seasonDetail.episodes[0].runtimeLabel)
        }

    @Test
    fun `given api throws - when viewmodel is created - then emits network error`() =
        runTest {
            val viewModel = buildViewModel(FakeTvShowsRepository(error = Exception("Network")))

            val state = viewModel.uiState.value
            assertIs<SeasonDetailUiState.Error>(state)
            assertEquals(AppError.NetworkError, state.error)
        }

    @Test
    fun `given error - when retry - then emits success`() =
        runTest {
            val repo = FakeTvShowsRepository(error = Exception("Network"))
            val viewModel = buildViewModel(repo)
            assertIs<SeasonDetailUiState.Error>(viewModel.uiState.value)

            repo.error = null
            repo.seasonDetail = seasonDetail
            viewModel.loadSeasonDetail()

            val state = viewModel.uiState.value
            assertIs<SeasonDetailUiState.Success>(state)
            assertEquals("Season 1", state.seasonDetail.name)
        }
}
