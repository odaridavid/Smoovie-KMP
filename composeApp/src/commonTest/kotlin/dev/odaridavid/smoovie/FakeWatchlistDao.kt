package dev.odaridavid.smoovie

import dev.odaridavid.smoovie.watchlist.data.WatchlistDao
import dev.odaridavid.smoovie.watchlist.data.WatchlistItemEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

internal class FakeWatchlistDao : WatchlistDao {
    private val entries = MutableStateFlow<List<WatchlistItemEntity>>(emptyList())

    override fun observeAll(): Flow<List<WatchlistItemEntity>> = entries.map { list -> list.sortedByDescending { it.addedAt } }

    override fun observeContains(
        id: Int,
        mediaType: String,
    ): Flow<Boolean> = entries.map { list -> list.any { it.id == id && it.mediaType == mediaType } }

    override suspend fun contains(
        id: Int,
        mediaType: String,
    ): Boolean = entries.value.any { it.id == id && it.mediaType == mediaType }

    override suspend fun insert(entry: WatchlistItemEntity) {
        entries.update { current ->
            current.filterNot { it.id == entry.id && it.mediaType == entry.mediaType } + entry
        }
    }

    override suspend fun deleteById(
        id: Int,
        mediaType: String,
    ) {
        entries.update { current -> current.filterNot { it.id == id && it.mediaType == mediaType } }
    }
}
