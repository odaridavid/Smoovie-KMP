package dev.odaridavid.smoovie.filter

enum class MovieSortOption(
    val apiValue: String,
    val label: String,
) {
    POPULARITY("popularity.desc", "Popularity"),
    RATING("vote_average.desc", "Rating"),
    NEWEST("primary_release_date.desc", "Newest"),
    REVENUE("revenue.desc", "Revenue"),
}

enum class TvSortOption(
    val apiValue: String,
    val label: String,
) {
    POPULARITY("popularity.desc", "Popularity"),
    RATING("vote_average.desc", "Rating"),
    NEWEST("first_air_date.desc", "Newest"),
    NAME_AZ("name.asc", "Name (A–Z)"),
}

data class MovieFilterPreferences(
    val selectedGenreId: Int? = null,
    val sortBy: MovieSortOption = MovieSortOption.POPULARITY,
    val minRating: Float = 0f,
) {
    val isActive: Boolean
        get() = selectedGenreId != null || sortBy != MovieSortOption.POPULARITY || minRating > 0f
}

data class TvFilterPreferences(
    val selectedGenreId: Int? = null,
    val sortBy: TvSortOption = TvSortOption.POPULARITY,
    val minRating: Float = 0f,
) {
    val isActive: Boolean
        get() = selectedGenreId != null || sortBy != TvSortOption.POPULARITY || minRating > 0f
}

data class FilterGenreOption(
    val id: Int,
    val name: String,
)

data class SortEntry(
    val label: String,
    val apiValue: String,
)
