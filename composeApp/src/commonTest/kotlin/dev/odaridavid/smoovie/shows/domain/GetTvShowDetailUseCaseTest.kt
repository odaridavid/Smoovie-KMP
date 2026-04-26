package dev.odaridavid.smoovie.shows.domain

import dev.odaridavid.smoovie.FakeTvShowsRepository
import dev.odaridavid.smoovie.configuration.ConfigurationStore
import dev.odaridavid.smoovie.shared.data.Keyword
import dev.odaridavid.smoovie.shared.data.WatchProvider
import dev.odaridavid.smoovie.shared.data.WatchProviderRegion
import dev.odaridavid.smoovie.shared.data.WatchProvidersResponse
import dev.odaridavid.smoovie.shows.data.TvKeywordsResponse
import dev.odaridavid.smoovie.shows.data.TvShowDetail
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetTvShowDetailUseCaseTest {
    private val baseDetail = TvShowDetail(id = 1, name = "Test Show")

    private fun buildUseCase(repo: FakeTvShowsRepository = FakeTvShowsRepository(tvShowDetail = baseDetail)) =
        GetTvShowDetailUseCase(repo, ConfigurationStore())

    @Test
    fun `given providers have DE region - when invoked - then uses DE streaming data`() =
        runTest {
            val repo =
                FakeTvShowsRepository(
                    tvShowDetail = baseDetail,
                    watchProviders =
                        WatchProvidersResponse(
                            id = 1,
                            results =
                                mapOf(
                                    "DE" to WatchProviderRegion(
                                        flatrate = listOf(WatchProvider(providerId = 1, providerName = "Netflix", displayPriority = 1)),
                                    ),
                                    "US" to WatchProviderRegion(
                                        flatrate = listOf(WatchProvider(providerId = 2, providerName = "HBO", displayPriority = 1)),
                                    ),
                                ),
                        ),
                )

            val result = buildUseCase(repo).invoke(1, presentLabel = "Present")

            assertEquals(1, result.streamingProviders.size)
            assertEquals("Netflix", result.streamingProviders.first().name)
        }

    @Test
    fun `given no DE region but US present - when invoked - then falls back to US`() =
        runTest {
            val repo =
                FakeTvShowsRepository(
                    tvShowDetail = baseDetail,
                    watchProviders =
                        WatchProvidersResponse(
                            id = 1,
                            results =
                                mapOf(
                                    "US" to WatchProviderRegion(
                                        flatrate = listOf(WatchProvider(providerId = 2, providerName = "HBO Max", displayPriority = 1)),
                                    ),
                                ),
                        ),
                )

            val result = buildUseCase(repo).invoke(1, presentLabel = "Present")

            assertEquals(1, result.streamingProviders.size)
            assertEquals("HBO Max", result.streamingProviders.first().name)
        }

    @Test
    fun `given neither DE nor US - when invoked - then uses first available region`() =
        runTest {
            val repo =
                FakeTvShowsRepository(
                    tvShowDetail = baseDetail,
                    watchProviders =
                        WatchProvidersResponse(
                            id = 1,
                            results =
                                mapOf(
                                    "FR" to WatchProviderRegion(
                                        flatrate = listOf(WatchProvider(providerId = 3, providerName = "Canal+", displayPriority = 1)),
                                    ),
                                ),
                        ),
                )

            val result = buildUseCase(repo).invoke(1, presentLabel = "Present")

            assertEquals(1, result.streamingProviders.size)
            assertEquals("Canal+", result.streamingProviders.first().name)
        }

    @Test
    fun `given empty providers map - when invoked - then streaming and rentBuy are empty`() =
        runTest {
            val result = buildUseCase().invoke(1, presentLabel = "Present")

            assertTrue(result.streamingProviders.isEmpty())
            assertTrue(result.rentBuyProviders.isEmpty())
        }

    @Test
    fun `given providers fetch fails - when invoked - then streaming and rentBuy are empty without throwing`() =
        runTest {
            val delegate = FakeTvShowsRepository(tvShowDetail = baseDetail)
            val repo =
                object : TvShowsRepository by delegate {
                    override suspend fun getWatchProviders(tvShowId: Int): WatchProvidersResponse =
                        throw RuntimeException("network error")
                }

            val result = GetTvShowDetailUseCase(repo, ConfigurationStore()).invoke(1, presentLabel = "Present")

            assertTrue(result.streamingProviders.isEmpty())
            assertTrue(result.rentBuyProviders.isEmpty())
        }

    @Test
    fun `given keywords fetch fails - when invoked - then keywords are empty without throwing`() =
        runTest {
            val delegate = FakeTvShowsRepository(tvShowDetail = baseDetail)
            val repo =
                object : TvShowsRepository by delegate {
                    override suspend fun getKeywords(tvShowId: Int): TvKeywordsResponse =
                        throw RuntimeException("network error")
                }

            val result = GetTvShowDetailUseCase(repo, ConfigurationStore()).invoke(1, presentLabel = "Present")

            assertTrue(result.keywords.isEmpty())
        }

    @Test
    fun `given more than 3 keywords - when invoked - then keywords are capped at 3`() =
        runTest {
            val repo =
                FakeTvShowsRepository(
                    tvShowDetail = baseDetail,
                    keywords =
                        TvKeywordsResponse(
                            id = 1,
                            results = (1..5).map { Keyword(id = it, name = "keyword$it") },
                        ),
                )

            val result = buildUseCase(repo).invoke(1, presentLabel = "Present")

            assertEquals(3, result.keywords.size)
            assertEquals(listOf("keyword1", "keyword2", "keyword3"), result.keywords)
        }

    @Test
    fun `given keywords in results field - when invoked - then keywords are read from results`() =
        runTest {
            val repo =
                FakeTvShowsRepository(
                    tvShowDetail = baseDetail,
                    keywords =
                        TvKeywordsResponse(
                            id = 1,
                            results = listOf(Keyword(id = 1, name = "sci-fi"), Keyword(id = 2, name = "space")),
                        ),
                )

            val result = buildUseCase(repo).invoke(1, presentLabel = "Present")

            assertEquals(listOf("sci-fi", "space"), result.keywords)
        }

    @Test
    fun `given no keywords - when invoked - then keywords list is empty`() =
        runTest {
            val result = buildUseCase().invoke(1, presentLabel = "Present")

            assertTrue(result.keywords.isEmpty())
        }

    @Test
    fun `given streaming provider also in rent list - when invoked - then excluded from rentBuy`() =
        runTest {
            val netflix = WatchProvider(providerId = 1, providerName = "Netflix", displayPriority = 1)
            val repo =
                FakeTvShowsRepository(
                    tvShowDetail = baseDetail,
                    watchProviders =
                        WatchProvidersResponse(
                            id = 1,
                            results =
                                mapOf(
                                    "DE" to WatchProviderRegion(
                                        flatrate = listOf(netflix),
                                        rent = listOf(netflix),
                                    ),
                                ),
                        ),
                )

            val result = buildUseCase(repo).invoke(1, presentLabel = "Present")

            assertEquals(listOf("Netflix"), result.streamingProviders.map { it.name })
            assertTrue(result.rentBuyProviders.isEmpty())
        }

    @Test
    fun `given same provider in rent and buy - when invoked - then appears only once in rentBuy`() =
        runTest {
            val amazon = WatchProvider(providerId = 5, providerName = "Amazon", displayPriority = 1)
            val repo =
                FakeTvShowsRepository(
                    tvShowDetail = baseDetail,
                    watchProviders =
                        WatchProvidersResponse(
                            id = 1,
                            results =
                                mapOf(
                                    "DE" to WatchProviderRegion(
                                        rent = listOf(amazon),
                                        buy = listOf(amazon),
                                    ),
                                ),
                        ),
                )

            val result = buildUseCase(repo).invoke(1, presentLabel = "Present")

            assertEquals(1, result.rentBuyProviders.size)
            assertEquals("Amazon", result.rentBuyProviders.first().name)
        }

    @Test
    fun `given streaming providers with different priorities - when invoked - then ordered by priority ascending`() =
        runTest {
            val repo =
                FakeTvShowsRepository(
                    tvShowDetail = baseDetail,
                    watchProviders =
                        WatchProvidersResponse(
                            id = 1,
                            results =
                                mapOf(
                                    "DE" to WatchProviderRegion(
                                        flatrate =
                                            listOf(
                                                WatchProvider(providerId = 2, providerName = "Disney+", displayPriority = 2),
                                                WatchProvider(providerId = 1, providerName = "Netflix", displayPriority = 1),
                                            ),
                                    ),
                                ),
                        ),
                )

            val result = buildUseCase(repo).invoke(1, presentLabel = "Present")

            assertEquals(listOf("Netflix", "Disney+"), result.streamingProviders.map { it.name })
        }

    @Test
    fun `given watch providers link in region - when invoked - then link is passed through`() =
        runTest {
            val repo =
                FakeTvShowsRepository(
                    tvShowDetail = baseDetail,
                    watchProviders =
                        WatchProvidersResponse(
                            id = 1,
                            results =
                                mapOf(
                                    "DE" to WatchProviderRegion(link = "https://www.themoviedb.org/tv/1/watch"),
                                ),
                        ),
                )

            val result = buildUseCase(repo).invoke(1, presentLabel = "Present")

            assertEquals("https://www.themoviedb.org/tv/1/watch", result.watchProvidersLink)
        }
}
