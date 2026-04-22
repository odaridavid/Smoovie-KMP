package dev.odaridavid.smoovie.watchlist.data

import dev.odaridavid.smoovie.utils.currentTimeMillis
import dev.odaridavid.smoovie.watchlist.domain.WatchlistEntry
import dev.odaridavid.smoovie.watchlist.domain.WatchlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class WatchlistRepositoryImpl(
    private val dao: WatchlistDao,
    private val now: () -> Long = { currentTimeMillis() },
) : WatchlistRepository {
    override fun observeAll(): Flow<List<WatchlistEntry>> = dao.observeAll().map { rows -> rows.map { it.toDomain() } }

    override fun observeContains(movieId: Int): Flow<Boolean> = dao.observeContains(movieId)

    override suspend fun toggle(entry: WatchlistEntry) {
        if (dao.contains(entry.id)) {
            dao.deleteById(entry.id)
        } else {
            dao.insert(entry.toEntity(addedAt = now()))
        }
    }

    override suspend fun remove(movieId: Int) {
        dao.deleteById(movieId)
    }
}

private fun WatchlistMovieEntity.toDomain() =
    WatchlistEntry(
        id = id,
        title = title,
        overview = overview,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        backdropUrl = backdropUrl,
        posterUrl = posterUrl,
    )

private fun WatchlistEntry.toEntity(addedAt: Long) =
    WatchlistMovieEntity(
        id = id,
        title = title,
        overview = overview,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        backdropUrl = backdropUrl,
        posterUrl = posterUrl,
        addedAt = addedAt,
    )
