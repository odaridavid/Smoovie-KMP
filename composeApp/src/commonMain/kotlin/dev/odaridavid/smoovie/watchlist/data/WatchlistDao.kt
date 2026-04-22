package dev.odaridavid.smoovie.watchlist.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
internal interface WatchlistDao {
    @Query("SELECT * FROM watchlist_movies ORDER BY addedAt DESC")
    fun observeAll(): Flow<List<WatchlistMovieEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist_movies WHERE id = :movieId)")
    fun observeContains(movieId: Int): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist_movies WHERE id = :movieId)")
    suspend fun contains(movieId: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: WatchlistMovieEntity)

    @Query("DELETE FROM watchlist_movies WHERE id = :movieId")
    suspend fun deleteById(movieId: Int)
}
