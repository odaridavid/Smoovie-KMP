package dev.odaridavid.smoovie.utils

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class TtlCache<K : Any, V : Any>(
    private val ttlMillis: Long,
    private val now: () -> Long = { currentTimeMillis() },
) {
    private val mutex = Mutex()
    private val entries = mutableMapOf<K, Entry<V>>()

    suspend fun getOrFetch(
        key: K,
        fetch: suspend () -> V,
    ): V {
        mutex.withLock {
            val cached = entries[key]
            if (cached != null && now() - cached.timestamp < ttlMillis) {
                return cached.value
            }
        }
        val fresh = fetch()
        mutex.withLock {
            entries[key] = Entry(fresh, now())
        }
        return fresh
    }

    suspend fun clear() =
        mutex.withLock {
            entries.clear()
        }

    private data class Entry<V>(
        val value: V,
        val timestamp: Long,
    )
}
