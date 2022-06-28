package org.ccci.gto.android.common.androidx.collection

import androidx.collection.lruCache
import org.junit.Assert.assertEquals
import org.junit.Test

class LruCacheTest {
    @Test
    fun testGetOrCreate() {
        val cache = lruCache<String, String>(2)
        cache.put("key1", "value1")

        assertEquals("value1", cache.getOrCreate("key1") { "value2" })
        assertEquals("value2", cache.getOrCreate("key2") { "value2" })
    }
}
