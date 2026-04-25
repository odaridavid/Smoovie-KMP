package dev.odaridavid.smoovie.storage

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import dev.odaridavid.smoovie.watchlist.data.WatchlistDao
import dev.odaridavid.smoovie.watchlist.data.WatchlistItemEntity

@Database(entities = [WatchlistItemEntity::class], version = 2, exportSchema = true)
@ConstructedBy(SmoovieDatabaseConstructor::class)
abstract class SmoovieDatabase : RoomDatabase() {
    internal abstract fun watchlistDao(): WatchlistDao
}

// Room KSP generates the actual implementation for each target.
@Suppress("NO_ACTUAL_FOR_EXPECT", "KotlinNoActualForExpect")
expect object SmoovieDatabaseConstructor : RoomDatabaseConstructor<SmoovieDatabase> {
    override fun initialize(): SmoovieDatabase
}

internal const val SMOOVIE_DATABASE_NAME = "smoovie.db"
