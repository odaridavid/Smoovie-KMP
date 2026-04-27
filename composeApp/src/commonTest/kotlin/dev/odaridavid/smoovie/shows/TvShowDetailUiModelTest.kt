package dev.odaridavid.smoovie.shows

import dev.odaridavid.smoovie.shared.data.AuthorDetails
import dev.odaridavid.smoovie.shared.data.CastMember
import dev.odaridavid.smoovie.shared.data.Credits
import dev.odaridavid.smoovie.shared.data.Review
import dev.odaridavid.smoovie.shared.data.ReviewsResponse
import dev.odaridavid.smoovie.shared.data.Video
import dev.odaridavid.smoovie.shared.data.VideosResponse
import dev.odaridavid.smoovie.shows.data.ContentRating
import dev.odaridavid.smoovie.shows.data.ContentRatingsResponse
import dev.odaridavid.smoovie.shows.data.Network
import dev.odaridavid.smoovie.shows.data.Season
import dev.odaridavid.smoovie.shows.data.TvGenre
import dev.odaridavid.smoovie.shows.data.TvShow
import dev.odaridavid.smoovie.shows.data.TvShowDetail
import dev.odaridavid.smoovie.shows.data.TvShowsResponse
import kotlin.test.Test
import kotlin.test.assertEquals

class TvShowDetailUiModelTest {
    // yearsRange

    @Test
    fun `given in-production show - when mapped - then years range ends with present label`() {
        val detail = tvShowDetail(firstAirDate = "2020-01-01", inProduction = true)

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null, presentLabel = "Present")

        assertEquals("2020 – Present", uiModel.yearsRange)
    }

    @Test
    fun `given ended show with same first and last year - when mapped - then years range is single year`() {
        val detail = tvShowDetail(firstAirDate = "2020-01-01", lastAirDate = "2020-12-31", inProduction = false)

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null, presentLabel = "Present")

        assertEquals("2020", uiModel.yearsRange)
    }

    @Test
    fun `given ended show with different first and last year - when mapped - then years range spans both`() {
        val detail = tvShowDetail(firstAirDate = "2020-01-01", lastAirDate = "2023-05-14", inProduction = false)

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null, presentLabel = "Present")

        assertEquals("2020 – 2023", uiModel.yearsRange)
    }

    @Test
    fun `given show with missing first air date - when mapped - then years range is empty`() {
        val detail = tvShowDetail(firstAirDate = "", lastAirDate = "2023-01-01", inProduction = false)

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null, presentLabel = "Present")

        assertEquals("", uiModel.yearsRange)
    }

    @Test
    fun `given show with missing last air date - when mapped - then years range is just first year`() {
        val detail = tvShowDetail(firstAirDate = "2020-01-01", lastAirDate = "", inProduction = false)

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null, presentLabel = "Present")

        assertEquals("2020", uiModel.yearsRange)
    }

    // seasonsLabel

    @Test
    fun `given show with no seasons - when mapped - then seasons label is empty`() {
        val detail = tvShowDetail(numberOfSeasons = 0, numberOfEpisodes = 0)

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null, presentLabel = "Present")

        assertEquals("", uiModel.seasonsLabel)
    }

    @Test
    fun `given show with 1 season and 1 episode - when mapped - then seasons label is singular`() {
        val detail = tvShowDetail(numberOfSeasons = 1, numberOfEpisodes = 1)

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null, presentLabel = "Present")

        assertEquals("1 season · 1 episode", uiModel.seasonsLabel)
    }

    @Test
    fun `given show with 1 season and multiple episodes - when mapped - then season is singular episodes plural`() {
        val detail = tvShowDetail(numberOfSeasons = 1, numberOfEpisodes = 10)

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null, presentLabel = "Present")

        assertEquals("1 season · 10 episodes", uiModel.seasonsLabel)
    }

    @Test
    fun `given show with multiple seasons and episodes - when mapped - then both are plural`() {
        val detail = tvShowDetail(numberOfSeasons = 3, numberOfEpisodes = 36)

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null, presentLabel = "Present")

        assertEquals("3 seasons · 36 episodes", uiModel.seasonsLabel)
    }

    // ageRating

    @Test
    fun `given DE content rating without FSK prefix - when mapped - then FSK prefix is added`() {
        val detail =
            tvShowDetail(
                contentRatings =
                    ContentRatingsResponse(
                        results = listOf(ContentRating(countryCode = "DE", rating = "16")),
                    ),
            )

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null, presentLabel = "Present")

        assertEquals("FSK 16", uiModel.ageRating)
    }

    @Test
    fun `given DE content rating with FSK prefix already - when mapped - then prefix is not doubled`() {
        val detail =
            tvShowDetail(
                contentRatings =
                    ContentRatingsResponse(
                        results = listOf(ContentRating(countryCode = "DE", rating = "FSK 16")),
                    ),
            )

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null, presentLabel = "Present")

        assertEquals("FSK 16", uiModel.ageRating)
    }

    @Test
    fun `given no DE content rating - when mapped - then age rating is empty`() {
        val detail =
            tvShowDetail(
                contentRatings =
                    ContentRatingsResponse(
                        results = listOf(ContentRating(countryCode = "US", rating = "TV-MA")),
                    ),
            )

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null, presentLabel = "Present")

        assertEquals("", uiModel.ageRating)
    }

    @Test
    fun `given blank DE content rating - when mapped - then age rating is empty`() {
        val detail =
            tvShowDetail(
                contentRatings =
                    ContentRatingsResponse(
                        results = listOf(ContentRating(countryCode = "DE", rating = "")),
                    ),
            )

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null, presentLabel = "Present")

        assertEquals("", uiModel.ageRating)
    }

    @Test
    fun `given no content ratings - when mapped - then age rating is empty`() {
        val detail = tvShowDetail(contentRatings = null)

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null, presentLabel = "Present")

        assertEquals("", uiModel.ageRating)
    }

    // seasons

    @Test
    fun `given seasons including season zero - when mapped - then season zero is filtered out`() {
        val detail =
            tvShowDetail(
                seasons =
                    listOf(
                        Season(id = 0, name = "Specials", seasonNumber = 0),
                        Season(id = 1, name = "Season 1", seasonNumber = 1),
                    ),
            )

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null, presentLabel = "Present")

        assertEquals(1, uiModel.seasons.size)
        assertEquals(1, uiModel.seasons.first().seasonNumber)
    }

    @Test
    fun `given season with poster path - when mapped - then resolver is used for poster url`() {
        val detail =
            tvShowDetail(
                seasons = listOf(Season(id = 1, name = "Season 1", seasonNumber = 1, posterPath = "/s1.jpg")),
            )

        val uiModel =
            detail.toDetailUiModel(
                backdropUrl = null,
                posterUrl = null,
                presentLabel = "Present",
                seasonPosterResolver = { path -> path?.let { "https://img.example$it" } },
            )

        assertEquals("https://img.example/s1.jpg", uiModel.seasons.first().posterUrl)
    }

    // genres and networks

    @Test
    fun `given genres - when mapped - then genres are comma joined`() {
        val detail = tvShowDetail(genres = listOf(TvGenre(1, "Drama"), TvGenre(2, "Thriller")))

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null, presentLabel = "Present")

        assertEquals("Drama, Thriller", uiModel.genres)
    }

    @Test
    fun `given networks - when mapped - then networks are comma joined`() {
        val detail = tvShowDetail(networks = listOf(Network(1, "HBO"), Network(2, "Netflix")))

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null, presentLabel = "Present")

        assertEquals("HBO, Netflix", uiModel.networks)
    }

    // voteCount

    @Test
    fun `given vote count - when mapped - then formatted with comma separators`() {
        val detail = tvShowDetail(voteCount = 34521)

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null, presentLabel = "Present")

        assertEquals("34,521", uiModel.voteCount)
    }

    // cast

    @Test
    fun `given cast members - when mapped - then sorted by order`() {
        val detail =
            tvShowDetail(
                credits =
                    Credits(
                        cast =
                            listOf(
                                CastMember(id = 2, name = "Actor B", order = 1),
                                CastMember(id = 1, name = "Actor A", order = 0),
                            ),
                    ),
            )

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null, presentLabel = "Present")

        assertEquals(listOf("Actor A", "Actor B"), uiModel.cast.map { it.name })
    }

    @Test
    fun `given more than 20 cast members - when mapped - then capped at 20`() {
        val detail =
            tvShowDetail(
                credits =
                    Credits(
                        cast = (1..25).map { CastMember(id = it, name = "Actor $it", order = it) },
                    ),
            )

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null, presentLabel = "Present")

        assertEquals(20, uiModel.cast.size)
    }

    @Test
    fun `given no credits - when mapped - then cast is empty`() {
        val detail = tvShowDetail(credits = null)

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null, presentLabel = "Present")

        assertEquals(emptyList(), uiModel.cast)
    }

    // reviews

    @Test
    fun `given no reviews - when mapped - then reviews is empty`() {
        val detail = tvShowDetail(reviews = null)

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null, presentLabel = "Present")

        assertEquals(emptyList(), uiModel.reviews)
    }

    @Test
    fun `given more reviews than display limit - when mapped - then reviews are capped at 3`() {
        val detail =
            tvShowDetail(
                reviews =
                    ReviewsResponse(
                        results =
                            (1..5).map {
                                Review(id = "r$it", author = "Author $it", content = "Content $it")
                            },
                    ),
            )

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null, presentLabel = "Present")

        assertEquals(3, uiModel.reviews.size)
        assertEquals(listOf("r1", "r2", "r3"), uiModel.reviews.map { it.id })
    }

    @Test
    fun `given review with blank author - when mapped - then falls back to username`() {
        val detail =
            tvShowDetail(
                reviews =
                    ReviewsResponse(
                        results =
                            listOf(
                                Review(
                                    id = "r1",
                                    author = "",
                                    content = "Great show",
                                    authorDetails = AuthorDetails(username = "fallback_user", rating = 9.0),
                                ),
                            ),
                    ),
            )

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null, presentLabel = "Present")

        assertEquals("fallback_user", uiModel.reviews.first().author)
    }

    @Test
    fun `given review with blank content - when mapped - then it is filtered out`() {
        val detail =
            tvShowDetail(
                reviews =
                    ReviewsResponse(
                        results =
                            listOf(
                                Review(id = "r1", author = "A", content = "   "),
                                Review(id = "r2", author = "B", content = "Excellent"),
                            ),
                    ),
            )

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null, presentLabel = "Present")

        assertEquals(listOf("r2"), uiModel.reviews.map { it.id })
    }

    // trailers

    @Test
    fun `given no videos - when mapped - then trailers is empty`() {
        val detail = tvShowDetail(videos = null)

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null, presentLabel = "Present")

        assertEquals(emptyList(), uiModel.trailers)
    }

    @Test
    fun `given non-youtube videos - when mapped - then they are filtered out`() {
        val detail =
            tvShowDetail(
                videos =
                    VideosResponse(
                        results =
                            listOf(
                                Video(id = "v1", key = "abc", site = "Vimeo", type = "Trailer"),
                                Video(id = "v2", key = "xyz", site = "YouTube", type = "Trailer"),
                            ),
                    ),
            )

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null, presentLabel = "Present")

        assertEquals(listOf("v2"), uiModel.trailers.map { it.id })
    }

    @Test
    fun `given more trailers than display limit - when mapped - then capped at 5`() {
        val detail =
            tvShowDetail(
                videos =
                    VideosResponse(
                        results =
                            (1..8).map {
                                Video(id = "v$it", key = "k$it", site = "YouTube", type = "Trailer", official = true)
                            },
                    ),
            )

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null, presentLabel = "Present")

        assertEquals(5, uiModel.trailers.size)
    }

    @Test
    fun `given mixed video types - when mapped - then trailers ordered before teasers and official first`() {
        val detail =
            tvShowDetail(
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

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null, presentLabel = "Present")

        assertEquals(listOf("v3", "v2", "v1"), uiModel.trailers.map { it.id })
    }

    @Test
    fun `given youtube video - when mapped - then thumbnail url and video key are set`() {
        val detail =
            tvShowDetail(
                videos =
                    VideosResponse(
                        results = listOf(Video(id = "v1", key = "abc123", site = "YouTube", type = "Trailer")),
                    ),
            )

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null, presentLabel = "Present")
        val trailer = uiModel.trailers.first()

        assertEquals("https://img.youtube.com/vi/abc123/mqdefault.jpg", trailer.thumbnailUrl)
        assertEquals("abc123", trailer.videoKey)
    }

    // similar rail

    @Test
    fun `given no recommendations or similar - when mapped - then similar rail is empty`() {
        val detail = tvShowDetail()

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null, presentLabel = "Present")

        assertEquals(emptyList(), uiModel.similar)
    }

    @Test
    fun `given recommendations - when mapped - then rail is populated in recommendation order`() {
        val detail =
            tvShowDetail(
                recommendations = tvShowsResponse((2..4).map { tvShow(id = it) }),
            )

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null, presentLabel = "Present")

        assertEquals(listOf(2, 3, 4), uiModel.similar.map { it.id })
    }

    @Test
    fun `given recommendations and similar - when mapped - then recommendations come first and fill from similar`() {
        val detail =
            tvShowDetail(
                recommendations = tvShowsResponse(listOf(tvShow(id = 10), tvShow(id = 11))),
                similar = tvShowsResponse(listOf(tvShow(id = 20), tvShow(id = 21), tvShow(id = 22))),
            )

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null, presentLabel = "Present")

        assertEquals(listOf(10, 11, 20, 21, 22), uiModel.similar.map { it.id })
    }

    @Test
    fun `given overlap between recommendations and similar - when mapped - then duplicates are removed`() {
        val detail =
            tvShowDetail(
                recommendations = tvShowsResponse(listOf(tvShow(id = 10), tvShow(id = 11))),
                similar = tvShowsResponse(listOf(tvShow(id = 11), tvShow(id = 12))),
            )

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null, presentLabel = "Present")

        assertEquals(listOf(10, 11, 12), uiModel.similar.map { it.id })
    }

    @Test
    fun `given current show appears in similar feed - when mapped - then it is excluded`() {
        val detail =
            tvShowDetail(
                recommendations = tvShowsResponse(listOf(tvShow(id = 1), tvShow(id = 2))),
            )

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null, presentLabel = "Present")

        assertEquals(listOf(2), uiModel.similar.map { it.id })
    }

    @Test
    fun `given more than five combined - when mapped - then rail is capped at 5`() {
        val detail =
            tvShowDetail(
                recommendations = tvShowsResponse((2..5).map { tvShow(id = it) }),
                similar = tvShowsResponse((6..10).map { tvShow(id = it) }),
            )

        val uiModel = detail.toDetailUiModel(backdropUrl = null, posterUrl = null, presentLabel = "Present")

        assertEquals(5, uiModel.similar.size)
        assertEquals(listOf(2, 3, 4, 5, 6), uiModel.similar.map { it.id })
    }

    @Test
    fun `given similar show with poster path - when mapped - then resolver is used for poster url`() {
        val detail =
            tvShowDetail(
                recommendations = tvShowsResponse(listOf(tvShow(id = 2, posterPath = "/abc.jpg"))),
            )

        val uiModel =
            detail.toDetailUiModel(
                backdropUrl = null,
                posterUrl = null,
                presentLabel = "Present",
                similarPosterResolver = { path -> path?.let { "https://img.example/$it" } },
            )

        assertEquals("https://img.example//abc.jpg", uiModel.similar.first().posterUrl)
    }

    @Test
    fun `given backdrop and poster urls - when mapped - then urls are set`() {
        val detail = tvShowDetail()

        val uiModel =
            detail.toDetailUiModel(
                backdropUrl = "https://example.com/backdrop.jpg",
                posterUrl = "https://example.com/poster.jpg",
                presentLabel = "Present",
            )

        assertEquals("https://example.com/backdrop.jpg", uiModel.backdropUrl)
        assertEquals("https://example.com/poster.jpg", uiModel.posterUrl)
    }

    // helpers

    private fun tvShowDetail(
        firstAirDate: String = "2020-01-01",
        lastAirDate: String = "2023-12-31",
        inProduction: Boolean = false,
        numberOfSeasons: Int = 0,
        numberOfEpisodes: Int = 0,
        voteCount: Int = 0,
        genres: List<TvGenre> = emptyList(),
        networks: List<Network> = emptyList(),
        seasons: List<Season> = emptyList(),
        credits: Credits? = null,
        reviews: ReviewsResponse? = null,
        videos: VideosResponse? = null,
        recommendations: TvShowsResponse? = null,
        similar: TvShowsResponse? = null,
        contentRatings: ContentRatingsResponse? = null,
    ) = TvShowDetail(
        id = 1,
        name = "Test Show",
        firstAirDate = firstAirDate,
        lastAirDate = lastAirDate,
        inProduction = inProduction,
        numberOfSeasons = numberOfSeasons,
        numberOfEpisodes = numberOfEpisodes,
        voteCount = voteCount,
        genres = genres,
        networks = networks,
        seasons = seasons,
        credits = credits,
        reviews = reviews,
        videos = videos,
        recommendations = recommendations,
        similar = similar,
        contentRatings = contentRatings,
    )

    private fun tvShow(
        id: Int,
        name: String = "Show $id",
        posterPath: String? = null,
    ) = TvShow(id = id, name = name, posterPath = posterPath)

    private fun tvShowsResponse(results: List<TvShow>) =
        TvShowsResponse(page = 1, results = results, totalPages = 1, totalResults = results.size)
}
