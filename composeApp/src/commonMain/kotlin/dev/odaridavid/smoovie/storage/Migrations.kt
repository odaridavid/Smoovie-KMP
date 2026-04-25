package dev.odaridavid.smoovie.storage

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

internal val MIGRATION_1_2 =
    object : Migration(1, 2) {
        override fun migrate(connection: SQLiteConnection) {
            connection.execSQL(
                "CREATE TABLE IF NOT EXISTS `watchlist_items` (" +
                    "`id` INTEGER NOT NULL, " +
                    "`title` TEXT NOT NULL, " +
                    "`overview` TEXT NOT NULL, " +
                    "`releaseDate` TEXT NOT NULL, " +
                    "`voteAverage` TEXT NOT NULL, " +
                    "`backdropUrl` TEXT, " +
                    "`posterUrl` TEXT, " +
                    "`addedAt` INTEGER NOT NULL, " +
                    "`mediaType` TEXT NOT NULL, " +
                    "PRIMARY KEY(`id`, `mediaType`))",
            )
            connection.execSQL(
                "INSERT INTO `watchlist_items` " +
                    "(id, title, overview, releaseDate, voteAverage, backdropUrl, posterUrl, addedAt, mediaType) " +
                    "SELECT id, title, overview, releaseDate, voteAverage, backdropUrl, posterUrl, addedAt, 'movie' " +
                    "FROM `watchlist_movies`",
            )
            connection.execSQL("DROP TABLE `watchlist_movies`")
        }
    }
