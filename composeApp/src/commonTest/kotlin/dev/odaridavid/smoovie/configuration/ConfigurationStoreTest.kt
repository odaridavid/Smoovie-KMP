package dev.odaridavid.smoovie.configuration

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ConfigurationStoreTest {
    private val testConfig =
        ImagesConfiguration(
            baseUrl = "http://image.tmdb.org/t/p/",
            secureBaseUrl = "https://image.tmdb.org/t/p/",
        )

    @Test
    fun `given null path - when building backdrop url - then returns null`() {
        val store = ConfigurationStore()
        store.save(testConfig)

        assertNull(store.backdropUrl(null))
    }

    @Test
    fun `given valid path - when building backdrop url with default size - then returns medium url`() {
        val store = ConfigurationStore()
        store.save(testConfig)

        assertEquals(
            "https://image.tmdb.org/t/p/w780/abc.jpg",
            store.backdropUrl("/abc.jpg"),
        )
    }

    @Test
    fun `given valid path - when building backdrop url with specific size - then uses that size`() {
        val store = ConfigurationStore()
        store.save(testConfig)

        assertEquals(
            "https://image.tmdb.org/t/p/original/abc.jpg",
            store.backdropUrl("/abc.jpg", BackdropSize.ORIGINAL),
        )
    }

    @Test
    fun `given null path - when building poster url - then returns null`() {
        val store = ConfigurationStore()
        store.save(testConfig)

        assertNull(store.posterUrl(null))
    }

    @Test
    fun `given valid path - when building poster url with default size - then returns medium url`() {
        val store = ConfigurationStore()
        store.save(testConfig)

        assertEquals(
            "https://image.tmdb.org/t/p/w342/poster.jpg",
            store.posterUrl("/poster.jpg"),
        )
    }

    @Test
    fun `given valid path - when building poster url with specific size - then uses that size`() {
        val store = ConfigurationStore()
        store.save(testConfig)

        assertEquals(
            "https://image.tmdb.org/t/p/w92/poster.jpg",
            store.posterUrl("/poster.jpg", PosterSize.TINY),
        )
    }
}
