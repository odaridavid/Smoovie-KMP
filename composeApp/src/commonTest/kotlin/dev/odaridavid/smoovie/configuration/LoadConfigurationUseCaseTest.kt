package dev.odaridavid.smoovie.configuration

import dev.odaridavid.smoovie.FakeConfigurationRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LoadConfigurationUseCaseTest {
    @Test
    fun `given repository returns config - when invoked - then saves to store`() =
        runTest {
            val store = ConfigurationStore()
            val repo = FakeConfigurationRepository()
            val useCase = LoadConfigurationUseCase(repo, store)

            useCase()

            assertEquals(
                "https://image.tmdb.org/t/p/w780/path.jpg",
                store.backdropUrl("/path.jpg"),
            )
        }

    @Test
    fun `given config loaded - when building poster url - then uses secure base url`() =
        runTest {
            val store = ConfigurationStore()
            val repo = FakeConfigurationRepository()
            val useCase = LoadConfigurationUseCase(repo, store)

            useCase()

            assertEquals(
                "https://image.tmdb.org/t/p/w342/poster.jpg",
                store.posterUrl("/poster.jpg"),
            )
        }

    @Test
    fun `given config not yet loaded - when building urls with null path - then returns null`() =
        runTest {
            val store = ConfigurationStore()

            assertNull(store.backdropUrl(null))
            assertNull(store.posterUrl(null))
        }

    @Test
    fun `given repository throws - when invoked - then propagates exception`() =
        runTest {
            val store = ConfigurationStore()
            val repo = FakeConfigurationRepository(error = Exception("Config unavailable"))
            val useCase = LoadConfigurationUseCase(repo, store)

            runCatching { useCase() }.also { assertTrue(it.isFailure) }
        }
}
