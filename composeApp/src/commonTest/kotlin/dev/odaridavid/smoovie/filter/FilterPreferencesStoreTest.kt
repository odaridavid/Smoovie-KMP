package dev.odaridavid.smoovie.filter

import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FilterPreferencesStoreTest {
    private fun buildStore(settings: MapSettings = MapSettings()) = FilterPreferencesStoreImpl(settings)

    @Test
    fun `given no saved filter - when getMovieFilter - then returns defaults`() =
        runTest {
            val store = buildStore()

            val result = store.getMovieFilter()

            assertNull(result.selectedGenreId)
            assertEquals(MovieSortOption.POPULARITY, result.sortBy)
            assertEquals(0f, result.minRating)
        }

    @Test
    fun `given no saved filter - when getTvFilter - then returns defaults`() =
        runTest {
            val store = buildStore()

            val result = store.getTvFilter()

            assertNull(result.selectedGenreId)
            assertEquals(TvSortOption.POPULARITY, result.sortBy)
            assertEquals(0f, result.minRating)
        }

    @Test
    fun `given movie filter saved - when getMovieFilter - then returns saved values`() =
        runTest {
            val store = buildStore()
            val prefs = MovieFilterPreferences(selectedGenreId = 28, sortBy = MovieSortOption.RATING, minRating = 7.5f)

            store.saveMovieFilter(prefs)
            val result = store.getMovieFilter()

            assertEquals(28, result.selectedGenreId)
            assertEquals(MovieSortOption.RATING, result.sortBy)
            assertEquals(7.5f, result.minRating)
        }

    @Test
    fun `given tv filter saved - when getTvFilter - then returns saved values`() =
        runTest {
            val store = buildStore()
            val prefs = TvFilterPreferences(selectedGenreId = 18, sortBy = TvSortOption.NAME_AZ, minRating = 5f)

            store.saveTvFilter(prefs)
            val result = store.getTvFilter()

            assertEquals(18, result.selectedGenreId)
            assertEquals(TvSortOption.NAME_AZ, result.sortBy)
            assertEquals(5f, result.minRating)
        }

    @Test
    fun `given movie filter saved with null genre - when getMovieFilter - then genre is null`() =
        runTest {
            val store = buildStore()
            store.saveMovieFilter(MovieFilterPreferences(selectedGenreId = 28))
            store.saveMovieFilter(MovieFilterPreferences(selectedGenreId = null))

            val result = store.getMovieFilter()

            assertNull(result.selectedGenreId)
        }

    @Test
    fun `given movie and tv filters saved - when reading each - then they are independent`() =
        runTest {
            val settings = MapSettings()
            val store = buildStore(settings)
            val moviePrefs = MovieFilterPreferences(selectedGenreId = 28, sortBy = MovieSortOption.REVENUE)
            val tvPrefs = TvFilterPreferences(selectedGenreId = 18, sortBy = TvSortOption.NAME_AZ)

            store.saveMovieFilter(moviePrefs)
            store.saveTvFilter(tvPrefs)

            val movie = store.getMovieFilter()
            val tv = store.getTvFilter()
            assertEquals(28, movie.selectedGenreId)
            assertEquals(MovieSortOption.REVENUE, movie.sortBy)
            assertEquals(18, tv.selectedGenreId)
            assertEquals(TvSortOption.NAME_AZ, tv.sortBy)
        }

    @Test
    fun `given movie filter saved - when getTvFilter - then tv filter is unchanged`() =
        runTest {
            val store = buildStore()
            store.saveMovieFilter(MovieFilterPreferences(selectedGenreId = 28, sortBy = MovieSortOption.RATING))

            val tvResult = store.getTvFilter()

            assertNull(tvResult.selectedGenreId)
            assertEquals(TvSortOption.POPULARITY, tvResult.sortBy)
        }
}
