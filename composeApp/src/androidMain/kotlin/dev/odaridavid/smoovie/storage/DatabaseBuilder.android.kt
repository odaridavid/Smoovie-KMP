package dev.odaridavid.smoovie.storage

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

actual class DatabaseBuilderFactory(private val context: Context) {
    actual fun create(): RoomDatabase.Builder<SmoovieDatabase> {
        val dbFile = context.getDatabasePath(SMOOVIE_DATABASE_NAME)
        return Room.databaseBuilder<SmoovieDatabase>(
            context = context.applicationContext,
            name = dbFile.absolutePath,
        )
    }
}
