package dev.odaridavid.smoovie.watchlist.data

import dev.odaridavid.smoovie.utils.currentTimeMillis
import dev.odaridavid.smoovie.watchlist.domain.MediaType
import dev.odaridavid.smoovie.watchlist.domain.WatchlistEntry
import dev.odaridavid.smoovie.watchlist.domain.WatchlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class WatchlistRepositoryImpl(
    private val dao: WatchlistDao,
    private val now: () -> Long = { currentTimeMillis() },
) : WatchlistRepository {
    override fun observeAll(): Flow<List<WatchlistEntry>> = dao.observeAll().map { rows -> rows.map { it.toDomain() } }

    override fun observeContains(
        id: Int,
        mediaType: MediaType,
    ): Flow<Boolean> = dao.observeContains(id, mediaType.storageKey)

    override suspend fun toggle(entry: WatchlistEntry) {
        if (dao.contains(entry.id, entry.mediaType.storageKey)) {
            dao.deleteById(entry.id, entry.mediaType.storageKey)
        } else {
            dao.insert(entry.toEntity(addedAt = now()))
        }
    }

    override suspend fun remove(
        id: Int,
        mediaType: MediaType,
    ) {
        dao.deleteById(id, mediaType.storageKey)
    }
}

private fun WatchlistItemEntity.toDomain() =
    WatchlistEntry(
        id = id,
        title = title,
        overview = overview,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        backdropUrl = backdropUrl,
        posterUrl = posterUrl,
        mediaType = MediaType.fromStorageKey(mediaType),
    )

private fun WatchlistEntry.toEntity(addedAt: Long) =
    WatchlistItemEntity(
        id = id,
        title = title,
        overview = overview,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        backdropUrl = backdropUrl,
        posterUrl = posterUrl,
        addedAt = addedAt,
        mediaType = mediaType.storageKey,
    )
