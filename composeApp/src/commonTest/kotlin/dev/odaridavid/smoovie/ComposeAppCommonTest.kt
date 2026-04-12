package dev.odaridavid.smoovie

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import dev.odaridavid.smoovie.ui.movies.MoviesUiState
import dev.odaridavid.smoovie.data.model.Movie

class MoviesUiStateTest {

    @Test
    fun successStateHoldsMovies() {
        val movies = listOf(
            Movie(id = 1, title = "Interstellar", overview = "A space odyssey.", voteAverage = 8.6)
        )
        val state = MoviesUiState.Success(movies)
        assertIs<MoviesUiState.Success>(state)
        assertEquals(1, state.movies.size)
        assertEquals("Interstellar", state.movies.first().title)
    }

    @Test
    fun errorStateHoldsMessage() {
        val state = MoviesUiState.Error("Network error")
        assertIs<MoviesUiState.Error>(state)
        assertEquals("Network error", state.message)
    }
}