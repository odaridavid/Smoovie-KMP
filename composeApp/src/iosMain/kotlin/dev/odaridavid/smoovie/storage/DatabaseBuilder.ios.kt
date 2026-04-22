package dev.odaridavid.smoovie.storage

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual class DatabaseBuilderFactory {
    @OptIn(ExperimentalForeignApi::class)
    actual fun create(): RoomDatabase.Builder<SmoovieDatabase> {
        val documentsDir =
            NSFileManager.defaultManager
                .URLForDirectory(
                    directory = NSDocumentDirectory,
                    inDomain = NSUserDomainMask,
                    appropriateForURL = null,
                    create = true,
                    error = null,
                )?.path ?: error("Unable to resolve documents directory")
        val dbPath = "$documentsDir/$SMOOVIE_DATABASE_NAME"
        return Room.databaseBuilder<SmoovieDatabase>(name = dbPath)
    }
}
