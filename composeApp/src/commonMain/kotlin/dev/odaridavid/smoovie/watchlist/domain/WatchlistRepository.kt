package dev.odaridavid.smoovie.watchlist.domain

import kotlinx.coroutines.flow.Flow

interface WatchlistRepository {
    fun observeAll(): Flow<List<WatchlistEntry>>

    fun observeContains(movieId: Int): Flow<Boolean>

    suspend fun toggle(entry: WatchlistEntry)

    suspend fun remove(movieId: Int)
}
