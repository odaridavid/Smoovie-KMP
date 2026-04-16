package dev.odaridavid.smoovie.movies

import dev.odaridavid.smoovie.movies.data.AuthorDetails
import dev.odaridavid.smoovie.movies.data.Genre
import dev.odaridavid.smoovie.movies.data.MovieDetail
import dev.odaridavid.smoovie.movies.data.Review
import dev.odaridavid.smoovie.movies.data.ReviewsResponse
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
    fun `given movie detail with vote count - when mapped - then vote count is formatted`() {
        val detail = movieDetail(voteCount = 34521)

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null)

        assertEquals("34,521", uiModel.voteCount)
    }

    @Test
    fun `given no reviews - when mapped - then reviews is empty`() {
        val detail = movieDetail(reviews = null)

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null)

        assertEquals(emptyList(), uiModel.reviews)
    }

    @Test
    fun `given more reviews than display limit - when mapped - then reviews are capped at 3`() {
        val detail =
            movieDetail(
                reviews =
                    ReviewsResponse(
                        results =
                            (1..5).map {
                                Review(
                                    id = "r$it",
                                    author = "Author$it",
                                    content = "Review content $it",
                                )
                            },
                    ),
            )

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null)

        assertEquals(3, uiModel.reviews.size)
        assertEquals(listOf("r1", "r2", "r3"), uiModel.reviews.map { it.id })
    }

    @Test
    fun `given review with blank author - when mapped - then falls back to username`() {
        val detail =
            movieDetail(
                reviews =
                    ReviewsResponse(
                        results =
                            listOf(
                                Review(
                                    id = "r1",
                                    author = "",
                                    content = "Content",
                                    authorDetails = AuthorDetails(
                                        username = "fallback_user",
                                        rating = 8.0
                                    ),
                                ),
                            ),
                    ),
            )

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null)

        assertEquals("fallback_user", uiModel.reviews.first().author)
        assertEquals("8.0", uiModel.reviews.first().rating)
    }

    @Test
    fun `given review with blank content - when mapped - then it is filtered out`() {
        val detail =
            movieDetail(
                reviews =
                    ReviewsResponse(
                        results =
                            listOf(
                                Review(id = "r1", author = "A", content = "   "),
                                Review(id = "r2", author = "B", content = "Good movie"),
                            ),
                    ),
            )

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null)

        assertEquals(listOf("r2"), uiModel.reviews.map { it.id })
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
        reviews: ReviewsResponse? = null,
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
        reviews = reviews,
    )
}
