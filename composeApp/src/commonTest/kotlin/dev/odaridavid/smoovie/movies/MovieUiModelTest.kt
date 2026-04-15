package dev.odaridavid.smoovie.movies

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MovieUiModelTest {
    @Test
    fun `given a valid movie - when mapped to ui model - then all fields are mapped correctly`() {
        val movie =
            Movie(
                id = 1,
                title = "Interstellar",
                overview = "Space odyssey.",
                releaseDate = "2014-11-05",
                voteAverage = 8.6,
            )

        val uiModel = movie.toUiModel(backdropUrl = "https://example.com/backdrop.jpg")

        assertEquals(1, uiModel.id)
        assertEquals("Interstellar", uiModel.title)
        assertEquals("Space odyssey.", uiModel.overview)
        assertEquals("5 Nov 2014", uiModel.releaseDate)
        assertEquals("8.6", uiModel.voteAverage)
        assertEquals("https://example.com/backdrop.jpg", uiModel.backdropUrl)
    }

    @Test
    fun `given zero vote average - when mapped to ui model - then rating is empty`() {
        val movie = Movie(id = 1, title = "Test", overview = "", voteAverage = 0.0)

        val uiModel = movie.toUiModel(backdropUrl = null)

        assertEquals("", uiModel.voteAverage)
    }

    @Test
    fun `given fractional vote average - when mapped to ui model - then rating is rounded to one decimal`() {
        val movie = Movie(id = 1, title = "Test", overview = "", voteAverage = 7.123)

        val uiModel = movie.toUiModel(backdropUrl = null)

        assertEquals("7.1", uiModel.voteAverage)
    }

    @Test
    fun `given perfect 10 rating - when mapped to ui model - then rating shows 10 point 0`() {
        val movie = Movie(id = 1, title = "Test", overview = "", voteAverage = 10.0)

        val uiModel = movie.toUiModel(backdropUrl = null)

        assertEquals("10.0", uiModel.voteAverage)
    }

    @Test
    fun `given valid iso date - when mapped to ui model - then date is human readable`() {
        val movie = Movie(id = 1, title = "Test", overview = "", releaseDate = "2023-07-21")

        val uiModel = movie.toUiModel(backdropUrl = null)

        assertEquals("21 Jul 2023", uiModel.releaseDate)
    }

    @Test
    fun `given date with leading zero day - when mapped to ui model - then leading zero is stripped`() {
        val movie = Movie(id = 1, title = "Test", overview = "", releaseDate = "2023-01-09")

        val uiModel = movie.toUiModel(backdropUrl = null)

        assertEquals("9 Jan 2023", uiModel.releaseDate)
    }

    @Test
    fun `given empty release date - when mapped to ui model - then date is empty`() {
        val movie = Movie(id = 1, title = "Test", overview = "", releaseDate = "")

        val uiModel = movie.toUiModel(backdropUrl = null)

        assertEquals("", uiModel.releaseDate)
    }

    @Test
    fun `given malformed date - when mapped to ui model - then date is returned as is`() {
        val movie = Movie(id = 1, title = "Test", overview = "", releaseDate = "not-a-date")

        val uiModel = movie.toUiModel(backdropUrl = null)

        assertEquals("not-a-date", uiModel.releaseDate)
    }

    @Test
    fun `given null backdrop and poster urls - when mapped to ui model - then urls are null`() {
        val movie = Movie(id = 1, title = "Test", overview = "")

        val uiModel = movie.toUiModel(backdropUrl = null, posterUrl = null)

        assertNull(uiModel.backdropUrl)
        assertNull(uiModel.posterUrl)
    }

    @Test
    fun `given poster url - when mapped to ui model - then poster url is set`() {
        val movie = Movie(id = 1, title = "Test", overview = "")

        val uiModel =
            movie.toUiModel(
                backdropUrl = null,
                posterUrl = "https://example.com/poster.jpg",
            )

        assertEquals("https://example.com/poster.jpg", uiModel.posterUrl)
    }
}
