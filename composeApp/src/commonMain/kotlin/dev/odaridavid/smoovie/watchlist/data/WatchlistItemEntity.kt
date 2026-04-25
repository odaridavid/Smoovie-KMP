package dev.odaridavid.smoovie.watchlist.data

import androidx.room.Entity

@Entity(
    tableName = "watchlist_items",
    primaryKeys = ["id", "mediaType"],
)
internal data class WatchlistItemEntity(
    val id: Int,
    val title: String,
    val overview: String,
    val releaseDate: String,
    val voteAverage: String,
    val backdropUrl: String?,
    val posterUrl: String?,
    val addedAt: Long,
    val mediaType: String,
)
