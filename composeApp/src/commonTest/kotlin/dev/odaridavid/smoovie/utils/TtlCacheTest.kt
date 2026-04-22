package dev.odaridavid.smoovie.utils

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TtlCacheTest {
    @Test
    fun `given fresh entry - when getOrFetch called again - then returns cached without refetching`() =
        runTest {
            var calls = 0
            val clock = FakeClock(0L)
            val cache = TtlCache<Int, String>(ttlMillis = 1000L, now = clock::value)

            val first =
                cache.getOrFetch(1) {
                    calls++
                    "v1"
                }
            val second =
                cache.getOrFetch(1) {
                    calls++
                    "v2"
                }

            assertEquals("v1", first)
            assertEquals("v1", second)
            assertEquals(1, calls)
        }

    @Test
    fun `given entry older than ttl - when getOrFetch called - then refetches`() =
        runTest {
            var calls = 0
            val clock = FakeClock(0L)
            val cache = TtlCache<Int, String>(ttlMillis = 1000L, now = clock::value)
            cache.getOrFetch(1) {
                calls++
                "v1"
            }

            clock.value = 1001L
            val refreshed =
                cache.getOrFetch(1) {
                    calls++
                    "v2"
                }

            assertEquals("v2", refreshed)
            assertEquals(2, calls)
        }

    @Test
    fun `given different keys - when getOrFetch called - then both fetched and cached`() =
        runTest {
            var calls = 0
            val cache = TtlCache<Int, String>(ttlMillis = 1000L, now = { 0L })

            cache.getOrFetch(1) {
                calls++
                "a"
            }
            cache.getOrFetch(2) {
                calls++
                "b"
            }
            cache.getOrFetch(1) {
                calls++
                "a2"
            }

            assertEquals(2, calls)
        }

    @Test
    fun `given cleared cache - when getOrFetch called - then refetches`() =
        runTest {
            var calls = 0
            val cache = TtlCache<Int, String>(ttlMillis = 1000L, now = { 0L })
            cache.getOrFetch(1) {
                calls++
                "v1"
            }

            cache.clear()
            cache.getOrFetch(1) {
                calls++
                "v2"
            }

            assertEquals(2, calls)
        }
}

private class FakeClock(
    var value: Long,
)
