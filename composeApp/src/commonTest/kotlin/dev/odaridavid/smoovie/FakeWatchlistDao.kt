package dev.odaridavid.smoovie

import dev.odaridavid.smoovie.watchlist.data.WatchlistDao
import dev.odaridavid.smoovie.watchlist.data.WatchlistMovieEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

internal class FakeWatchlistDao : WatchlistDao {
    private val entries = MutableStateFlow<List<WatchlistMovieEntity>>(emptyList())

    override fun observeAll(): Flow<List<WatchlistMovieEntity>> = entries.map { list -> list.sortedByDescending { it.addedAt } }

    override fun observeContains(movieId: Int): Flow<Boolean> = entries.map { list -> list.any { it.id == movieId } }

    override suspend fun contains(movieId: Int): Boolean = entries.value.any { it.id == movieId }

    override suspend fun insert(entry: WatchlistMovieEntity) {
        entries.update { current -> current.filterNot { it.id == entry.id } + entry }
    }

    override suspend fun deleteById(movieId: Int) {
        entries.update { current -> current.filterNot { it.id == movieId } }
    }
}
