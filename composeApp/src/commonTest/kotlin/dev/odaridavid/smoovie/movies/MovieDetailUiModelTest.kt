package dev.odaridavid.smoovie.movies

import kotlin.test.Test
import kotlin.test.assertEquals

class MovieDetailUiModelTest {

    @Test
    fun `given movie detail with runtime - when mapped - then runtime is formatted as hours and minutes`() {
        val detail = movieDetail(runtime = 169)

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null)

        assertEquals("2h 49m", uiModel.runtime)
    }

    @Test
    fun `given movie detail with short runtime - when mapped - then runtime shows 0 hours`() {
        val detail = movieDetail(runtime = 45)

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null)

        assertEquals("0h 45m", uiModel.runtime)
    }

    @Test
    fun `given movie detail with null runtime - when mapped - then runtime is empty`() {
        val detail = movieDetail(runtime = null)

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null)

        assertEquals("", uiModel.runtime)
    }

    @Test
    fun `given movie detail with genres - when mapped - then genres are comma joined`() {
        val detail = movieDetail(genres = listOf(Genre(1, "Action"), Genre(2, "Thriller")))

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null)

        assertEquals("Action, Thriller", uiModel.genres)
    }

    @Test
    fun `given movie detail with empty genres - when mapped - then genres is empty`() {
        val detail = movieDetail(genres = emptyList())

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null)

        assertEquals("", uiModel.genres)
    }

    @Test
    fun `given movie detail with tagline - when mapped - then tagline is preserved`() {
        val detail = movieDetail(tagline = "In space no one can hear you scream.")

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null)

        assertEquals("In space no one can hear you scream.", uiModel.tagline)
    }

    @Test
    fun `given movie detail with vote count - when mapped - then vote count is stringified`() {
        val detail = movieDetail(voteCount = 34521)

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null)

        assertEquals("34521", uiModel.voteCount)
    }

    @Test
    fun `given backdrop and poster urls - when mapped - then urls are set`() {
        val detail = movieDetail()

        val uiModel = detail.toDetailUiModel(
            backdropUrl = "https://example.com/backdrop.jpg",
            posterUrl = "https://example.com/poster.jpg",
        )

        assertEquals("https://example.com/backdrop.jpg", uiModel.backdropUrl)
        assertEquals("https://example.com/poster.jpg", uiModel.posterUrl)
    }

    private fun movieDetail(
        runtime: Int? = 120,
        genres: List<Genre> = emptyList(),
        tagline: String = "",
        voteCount: Int = 0,
    ) = MovieDetail(
        id = 1,
        title = "Test Movie",
        overview = "Test overview",
        releaseDate = "2023-01-15",
        voteAverage = 7.5,
        voteCount = voteCount,
        runtime = runtime,
        tagline = tagline,
        genres = genres,
    )
}
