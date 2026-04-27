package dev.odaridavid.smoovie.filter

import com.russhwolf.settings.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface FilterPreferencesStore {
    suspend fun getMovieFilter(): MovieFilterPreferences

    suspend fun saveMovieFilter(prefs: MovieFilterPreferences)

    suspend fun getTvFilter(): TvFilterPreferences

    suspend fun saveTvFilter(prefs: TvFilterPreferences)
}

class FilterPreferencesStoreImpl(
    private val settings: Settings,
) : FilterPreferencesStore {
    override suspend fun getMovieFilter(): MovieFilterPreferences =
        withContext(Dispatchers.Default) {
            MovieFilterPreferences(
                selectedGenreId = settings.getIntOrNull(KEY_MOVIE_GENRE_ID),
                sortBy =
                    settings
                        .getStringOrNull(KEY_MOVIE_SORT_BY)
                        ?.let { runCatching { MovieSortOption.valueOf(it) }.getOrNull() }
                        ?: MovieSortOption.POPULARITY,
                minRating = settings.getFloat(KEY_MOVIE_MIN_RATING, DEFAULT_MIN_RATING),
            )
        }

    override suspend fun saveMovieFilter(prefs: MovieFilterPreferences) =
        withContext(Dispatchers.Default) {
            if (prefs.selectedGenreId != null) {
                settings.putInt(KEY_MOVIE_GENRE_ID, prefs.selectedGenreId)
            } else {
                settings.remove(KEY_MOVIE_GENRE_ID)
            }
            settings.putString(KEY_MOVIE_SORT_BY, prefs.sortBy.name)
            settings.putFloat(KEY_MOVIE_MIN_RATING, prefs.minRating)
        }

    override suspend fun getTvFilter(): TvFilterPreferences =
        withContext(Dispatchers.Default) {
            TvFilterPreferences(
                selectedGenreId = settings.getIntOrNull(KEY_TV_GENRE_ID),
                sortBy =
                    settings
                        .getStringOrNull(KEY_TV_SORT_BY)
                        ?.let { runCatching { TvSortOption.valueOf(it) }.getOrNull() }
                        ?: TvSortOption.POPULARITY,
                minRating = settings.getFloat(KEY_TV_MIN_RATING, DEFAULT_MIN_RATING),
            )
        }

    override suspend fun saveTvFilter(prefs: TvFilterPreferences) =
        withContext(Dispatchers.Default) {
            if (prefs.selectedGenreId != null) {
                settings.putInt(KEY_TV_GENRE_ID, prefs.selectedGenreId)
            } else {
                settings.remove(KEY_TV_GENRE_ID)
            }
            settings.putString(KEY_TV_SORT_BY, prefs.sortBy.name)
            settings.putFloat(KEY_TV_MIN_RATING, prefs.minRating)
        }

    private companion object {
        const val KEY_MOVIE_GENRE_ID = "movie_genre_id"
        const val KEY_MOVIE_SORT_BY = "movie_sort_by"
        const val KEY_MOVIE_MIN_RATING = "movie_min_rating"
        const val KEY_TV_GENRE_ID = "tv_genre_id"
        const val KEY_TV_SORT_BY = "tv_sort_by"
        const val KEY_TV_MIN_RATING = "tv_min_rating"
        const val DEFAULT_MIN_RATING = 0f
    }
}
