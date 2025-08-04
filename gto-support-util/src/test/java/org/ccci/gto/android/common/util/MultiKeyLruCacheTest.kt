package org.ccci.gto.android.common.util

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MultiKeyLruCacheTest {
    @Test
    fun testNormalUsage() {
        val cache = MultiKeyLruCache<String, String>(1)
        val sharedVal = "common value"

        // populate cache
        for (i in 0..99) cache.putMulti("KEY$i", sharedVal)

        // test cache
        assertEquals(100, cache.size())
        for (i in 0..99) assertEquals(sharedVal, cache["KEY$i"])

        // force eviction
        cache.putMulti("OTHERKEY", "new val")

        // test cache
        assertNotNull(cache["OTHERKEY"])
        assertEquals(1, cache.size())
        for (i in 0..99) {
            assertNull(cache["KEY$i"])
        }
    }
}
