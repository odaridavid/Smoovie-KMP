package dev.odaridavid.smoovie.storage

import androidx.room.RoomDatabase

expect class DatabaseBuilderFactory {
    fun create(): RoomDatabase.Builder<SmoovieDatabase>
}
