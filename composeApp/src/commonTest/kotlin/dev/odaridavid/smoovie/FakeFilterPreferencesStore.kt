package dev.odaridavid.smoovie

import dev.odaridavid.smoovie.filter.FilterPreferencesStore
import dev.odaridavid.smoovie.filter.MovieFilterPreferences
import dev.odaridavid.smoovie.filter.TvFilterPreferences

class FakeFilterPreferencesStore(
    var movieFilter: MovieFilterPreferences = MovieFilterPreferences(),
    var tvFilter: TvFilterPreferences = TvFilterPreferences(),
) : FilterPreferencesStore {
    override suspend fun getMovieFilter(): MovieFilterPreferences = movieFilter

    override suspend fun saveMovieFilter(prefs: MovieFilterPreferences) {
        movieFilter = prefs
    }

    override suspend fun getTvFilter(): TvFilterPreferences = tvFilter

    override suspend fun saveTvFilter(prefs: TvFilterPreferences) {
        tvFilter = prefs
    }
}
