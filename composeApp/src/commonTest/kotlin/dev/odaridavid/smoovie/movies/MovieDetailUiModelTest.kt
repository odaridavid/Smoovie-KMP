package dev.odaridavid.smoovie.movies

import dev.odaridavid.smoovie.movies.data.AuthorDetails
import dev.odaridavid.smoovie.movies.data.Genre
import dev.odaridavid.smoovie.movies.data.Movie
import dev.odaridavid.smoovie.movies.data.MovieDetail
import dev.odaridavid.smoovie.movies.data.MoviesResponse
import dev.odaridavid.smoovie.movies.data.Review
import dev.odaridavid.smoovie.movies.data.ReviewsResponse
import dev.odaridavid.smoovie.movies.data.Video
import dev.odaridavid.smoovie.movies.data.VideosResponse
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
    fun `given no videos - when mapped - then trailers is empty`() {
        val detail = movieDetail(videos = null)

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null)

        assertEquals(emptyList(), uiModel.trailers)
    }

    @Test
    fun `given non-youtube videos - when mapped - then they are filtered out`() {
        val detail =
            movieDetail(
                videos =
                    VideosResponse(
                        results =
                            listOf(
                                Video(id = "v1", key = "abc", site = "Vimeo", type = "Trailer"),
                                Video(id = "v2", key = "xyz", site = "YouTube", type = "Trailer"),
                            ),
                    ),
            )

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null)

        assertEquals(listOf("v2"), uiModel.trailers.map { it.id })
    }

    @Test
    fun `given more trailers than display limit - when mapped - then capped at 5`() {
        val detail =
            movieDetail(
                videos =
                    VideosResponse(
                        results =
                            (1..8).map {
                                Video(
                                    id = "v$it",
                                    key = "k$it",
                                    name = "Trailer $it",
                                    site = "YouTube",
                                    type = "Trailer",
                                    official = true,
                                )
                            },
                    ),
            )

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null)

        assertEquals(5, uiModel.trailers.size)
    }

    @Test
    fun `given mixed video types - when mapped - then trailers are ordered before teasers and official first`() {
        val detail =
            movieDetail(
                videos =
                    VideosResponse(
                        results =
                            listOf(
                                Video(id = "v1", key = "k1", site = "YouTube", type = "Teaser", official = true),
                                Video(id = "v2", key = "k2", site = "YouTube", type = "Trailer", official = false),
                                Video(id = "v3", key = "k3", site = "YouTube", type = "Trailer", official = true),
                            ),
                    ),
            )

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null)

        assertEquals(listOf("v3", "v2", "v1"), uiModel.trailers.map { it.id })
    }

    @Test
    fun `given youtube video - when mapped - then thumbnail url and video key are set`() {
        val detail =
            movieDetail(
                videos =
                    VideosResponse(
                        results =
                            listOf(
                                Video(id = "v1", key = "abc123", site = "YouTube", type = "Trailer"),
                            ),
                    ),
            )

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null)
        val trailer = uiModel.trailers.first()

        assertEquals("https://img.youtube.com/vi/abc123/mqdefault.jpg", trailer.thumbnailUrl)
        assertEquals("abc123", trailer.videoKey)
    }

    @Test
    fun `given no recommendations or similar - when mapped - then similar rail is empty`() {
        val detail = movieDetail()

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null)

        assertEquals(emptyList(), uiModel.similar)
    }

    @Test
    fun `given recommendations - when mapped - then rail is populated in recommendation order`() {
        val detail =
            movieDetail(
                recommendations = moviesResponse((2..4).map { movie(id = it, title = "Rec $it") }),
            )

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null)

        assertEquals(listOf(2, 3, 4), uiModel.similar.map { it.id })
    }

    @Test
    fun `given recommendations and similar - when mapped - then recommendations come first and fill from similar`() {
        val detail =
            movieDetail(
                recommendations = moviesResponse(listOf(movie(id = 10), movie(id = 11))),
                similar = moviesResponse(listOf(movie(id = 20), movie(id = 21), movie(id = 22))),
            )

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null)

        assertEquals(listOf(10, 11, 20, 21, 22), uiModel.similar.map { it.id })
    }

    @Test
    fun `given overlap between recommendations and similar - when mapped - then duplicates are removed`() {
        val detail =
            movieDetail(
                recommendations = moviesResponse(listOf(movie(id = 10), movie(id = 11))),
                similar = moviesResponse(listOf(movie(id = 11), movie(id = 12))),
            )

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null)

        assertEquals(listOf(10, 11, 12), uiModel.similar.map { it.id })
    }

    @Test
    fun `given current movie appears in similar feed - when mapped - then it is excluded`() {
        val detail =
            movieDetail(
                recommendations = moviesResponse(listOf(movie(id = 1), movie(id = 2))),
            )

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null)

        assertEquals(listOf(2), uiModel.similar.map { it.id })
    }

    @Test
    fun `given more than five combined - when mapped - then rail is capped at 5`() {
        val detail =
            movieDetail(
                recommendations = moviesResponse((2..5).map { movie(id = it) }),
                similar = moviesResponse((6..10).map { movie(id = it) }),
            )

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null)

        assertEquals(5, uiModel.similar.size)
        assertEquals(listOf(2, 3, 4, 5, 6), uiModel.similar.map { it.id })
    }

    @Test
    fun `given similar movie with poster path - when mapped - then resolver is used for poster url`() {
        val detail =
            movieDetail(
                recommendations = moviesResponse(listOf(movie(id = 2, posterPath = "/abc.jpg"))),
            )

        val uiModel = detail.toDetailUiModel(
            backdropUrl = null,
            posterUrl = null,
            moviePosterUrlResolver = { path -> path?.let { "https://img.example/$it" } },
        )

        assertEquals("https://img.example//abc.jpg", uiModel.similar.first().posterUrl)
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
        videos: VideosResponse? = null,
        recommendations: MoviesResponse? = null,
        similar: MoviesResponse? = null,
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
        videos = videos,
        recommendations = recommendations,
        similar = similar,
    )

    private fun movie(
        id: Int,
        title: String = "Movie $id",
        posterPath: String? = null,
    ) = Movie(
        id = id,
        title = title,
        overview = "",
        posterPath = posterPath,
        releaseDate = "2023-01-01",
        voteAverage = 7.0,
    )

    private fun moviesResponse(results: List<Movie>) =
        MoviesResponse(
            page = 1,
            results = results,
            totalPages = 1,
            totalResults = results.size,
        )
}
