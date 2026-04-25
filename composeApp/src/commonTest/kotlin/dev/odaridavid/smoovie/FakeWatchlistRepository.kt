package dev.odaridavid.smoovie

import dev.odaridavid.smoovie.watchlist.domain.MediaType
import dev.odaridavid.smoovie.watchlist.domain.WatchlistEntry
import dev.odaridavid.smoovie.watchlist.domain.WatchlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeWatchlistRepository : WatchlistRepository {
    private val entries = MutableStateFlow<List<WatchlistEntry>>(emptyList())

    override fun observeAll(): Flow<List<WatchlistEntry>> = entries.asStateFlow()

    override fun observeContains(
        id: Int,
        mediaType: MediaType,
    ): Flow<Boolean> =
        entries.asStateFlow().map { list ->
            list.any { it.id == id && it.mediaType == mediaType }
        }

    override suspend fun toggle(entry: WatchlistEntry) {
        entries.update { current ->
            if (current.any { it.id == entry.id && it.mediaType == entry.mediaType }) {
                current.filterNot { it.id == entry.id && it.mediaType == entry.mediaType }
            } else {
                current + entry
            }
        }
    }

    override suspend fun remove(
        id: Int,
        mediaType: MediaType,
    ) {
        entries.update { current ->
            current.filterNot { it.id == id && it.mediaType == mediaType }
        }
    }
}
