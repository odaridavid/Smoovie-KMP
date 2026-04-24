package dev.odaridavid.smoovie.watchlist.domain

import dev.odaridavid.smoovie.FakeWatchlistRepository
import dev.odaridavid.smoovie.movies.MovieUiModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
class RemoveFromWatchlistUseCaseTest {
    private val movie =
        MovieUiModel(
            id = 42,
            title = "Interstellar",
            overview = "",
            releaseDate = "",
            voteAverage = "",
            backdropUrl = null,
            posterUrl = null,
        )

    @Test
    fun `given movie in watchlist - when invoked - then movie is removed`() =
        runTest {
            val repo = FakeWatchlistRepository()
            repo.toggle(
                WatchlistEntry(
                    id = movie.id,
                    title = movie.title,
                    overview = movie.overview,
                    releaseDate = movie.releaseDate,
                    voteAverage = movie.voteAverage,
                    backdropUrl = movie.backdropUrl,
                    posterUrl = movie.posterUrl,
                ),
            )

            RemoveFromWatchlistUseCase(repo)(movie)

            assertFalse(repo.observeContains(movie.id).first())
        }

    @Test
    fun `given movie not in watchlist - when invoked - then is a no-op`() =
        runTest {
            val repo = FakeWatchlistRepository()

            RemoveFromWatchlistUseCase(repo)(movie)

            assertFalse(repo.observeContains(movie.id).first())
        }
}
