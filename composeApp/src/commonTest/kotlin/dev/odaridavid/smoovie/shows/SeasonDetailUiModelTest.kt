package dev.odaridavid.smoovie.shows

import dev.odaridavid.smoovie.shows.data.Episode
import dev.odaridavid.smoovie.shows.data.SeasonDetail
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SeasonDetailUiModelTest {
    // year

    @Test
    fun `given season with air date - when mapped - then year is extracted`() {
        val detail = seasonDetail(airDate = "2021-09-17")

        val uiModel = detail.toUiModel(posterUrl = null)

        assertEquals("2021", uiModel.year)
    }

    @Test
    fun `given season with null air date - when mapped - then year is empty`() {
        val detail = seasonDetail(airDate = null)

        val uiModel = detail.toUiModel(posterUrl = null)

        assertEquals("", uiModel.year)
    }

    // episodeCountLabel

    @Test
    fun `given season with 1 episode - when mapped - then label is singular`() {
        val detail = seasonDetail(episodes = listOf(episode(id = 1, episodeNumber = 1)))

        val uiModel = detail.toUiModel(posterUrl = null)

        assertEquals("1 episode", uiModel.episodeCountLabel)
    }

    @Test
    fun `given season with multiple episodes - when mapped - then label is plural`() {
        val detail = seasonDetail(episodes = (1..8).map { episode(id = it, episodeNumber = it) })

        val uiModel = detail.toUiModel(posterUrl = null)

        assertEquals("8 episodes", uiModel.episodeCountLabel)
    }

    @Test
    fun `given season with no episodes - when mapped - then label shows zero episodes`() {
        val detail = seasonDetail(episodes = emptyList())

        val uiModel = detail.toUiModel(posterUrl = null)

        assertEquals("0 episodes", uiModel.episodeCountLabel)
    }

    // episode ordering

    @Test
    fun `given episodes out of order - when mapped - then sorted by episode number`() {
        val detail =
            seasonDetail(
                episodes =
                    listOf(
                        episode(id = 3, episodeNumber = 3),
                        episode(id = 1, episodeNumber = 1),
                        episode(id = 2, episodeNumber = 2),
                    ),
            )

        val uiModel = detail.toUiModel(posterUrl = null)

        assertEquals(listOf(1, 2, 3), uiModel.episodes.map { it.episodeNumber })
    }

    // poster URL

    @Test
    fun `given poster url - when mapped - then poster url is passed through`() {
        val detail = seasonDetail()

        val uiModel = detail.toUiModel(posterUrl = "https://example.com/poster.jpg")

        assertEquals("https://example.com/poster.jpg", uiModel.posterUrl)
    }

    // still URL resolver

    @Test
    fun `given episode with still path - when mapped - then resolver is used for still url`() {
        val detail =
            seasonDetail(
                episodes = listOf(episode(id = 1, episodeNumber = 1, stillPath = "/still.jpg")),
            )

        val uiModel =
            detail.toUiModel(
                posterUrl = null,
                stillUrlResolver = { path -> path?.let { "https://img.example$it" } },
            )

        assertEquals("https://img.example/still.jpg", uiModel.episodes.first().stillUrl)
    }

    @Test
    fun `given episode with null still path - when mapped - then still url is null`() {
        val detail = seasonDetail(episodes = listOf(episode(id = 1, episodeNumber = 1, stillPath = null)))

        val uiModel = detail.toUiModel(posterUrl = null)

        assertEquals(null, uiModel.episodes.first().stillUrl)
    }

    // headerLabel

    @Test
    fun `given episode with name - when mapped - then header label includes episode number and name`() {
        val detail = seasonDetail(episodes = listOf(episode(id = 1, episodeNumber = 3, name = "The Rains of Castamere")))

        val uiModel = detail.toUiModel(posterUrl = null)

        assertEquals("Ep 3 · The Rains of Castamere", uiModel.episodes.first().headerLabel)
    }

    @Test
    fun `given episode with blank name - when mapped - then header label is just episode number`() {
        val detail = seasonDetail(episodes = listOf(episode(id = 1, episodeNumber = 5, name = "")))

        val uiModel = detail.toUiModel(posterUrl = null)

        assertEquals("Ep 5", uiModel.episodes.first().headerLabel)
    }

    // airDate

    @Test
    fun `given episode with air date - when mapped - then air date is human readable`() {
        val detail = seasonDetail(episodes = listOf(episode(id = 1, episodeNumber = 1, airDate = "2011-06-12")))

        val uiModel = detail.toUiModel(posterUrl = null)

        assertEquals("12 Jun 2011", uiModel.episodes.first().airDate)
    }

    @Test
    fun `given episode with null air date - when mapped - then air date is empty`() {
        val detail = seasonDetail(episodes = listOf(episode(id = 1, episodeNumber = 1, airDate = null)))

        val uiModel = detail.toUiModel(posterUrl = null)

        assertTrue(uiModel.episodes.first().airDate.isEmpty())
    }

    // runtimeLabel

    @Test
    fun `given episode with runtime - when mapped - then runtime label shows minutes`() {
        val detail = seasonDetail(episodes = listOf(episode(id = 1, episodeNumber = 1, runtime = 54)))

        val uiModel = detail.toUiModel(posterUrl = null)

        assertEquals("54 min", uiModel.episodes.first().runtimeLabel)
    }

    @Test
    fun `given episode with null runtime - when mapped - then runtime label is empty`() {
        val detail = seasonDetail(episodes = listOf(episode(id = 1, episodeNumber = 1, runtime = null)))

        val uiModel = detail.toUiModel(posterUrl = null)

        assertTrue(uiModel.episodes.first().runtimeLabel.isEmpty())
    }

    // voteAverage

    @Test
    fun `given episode with vote average - when mapped - then vote average is formatted`() {
        val detail = seasonDetail(episodes = listOf(episode(id = 1, episodeNumber = 1, voteAverage = 9.2)))

        val uiModel = detail.toUiModel(posterUrl = null)

        assertEquals("9.2", uiModel.episodes.first().voteAverage)
    }

    @Test
    fun `given episode with zero vote average - when mapped - then vote average is empty`() {
        val detail = seasonDetail(episodes = listOf(episode(id = 1, episodeNumber = 1, voteAverage = 0.0)))

        val uiModel = detail.toUiModel(posterUrl = null)

        assertTrue(uiModel.episodes.first().voteAverage.isEmpty())
    }

    // helpers

    private fun seasonDetail(
        airDate: String? = "2021-04-04",
        episodes: List<Episode> = emptyList(),
    ) = SeasonDetail(
        id = 1,
        name = "Season 1",
        seasonNumber = 1,
        airDate = airDate,
        episodes = episodes,
    )

    private fun episode(
        id: Int,
        episodeNumber: Int,
        name: String = "Episode $episodeNumber",
        airDate: String? = "2021-04-04",
        runtime: Int? = 45,
        voteAverage: Double = 8.0,
        stillPath: String? = null,
    ) = Episode(
        id = id,
        name = name,
        episodeNumber = episodeNumber,
        airDate = airDate,
        runtime = runtime,
        voteAverage = voteAverage,
        stillPath = stillPath,
    )
}
