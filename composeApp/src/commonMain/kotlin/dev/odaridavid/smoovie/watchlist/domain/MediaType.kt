package dev.odaridavid.smoovie.watchlist.domain

enum class MediaType(
    val storageKey: String,
) {
    MOVIE("movie"),
    TV("tv"),
    ;

    companion object {
        fun fromStorageKey(key: String): MediaType =
            entries.firstOrNull { it.storageKey == key }
                ?: error("Unknown media type: $key")
    }
}
