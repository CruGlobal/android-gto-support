package org.ccci.gto.android.common.androidx.collection

import androidx.collection.lruCache
import org.junit.Assert.assertEquals
import org.junit.Test

class LruCacheTest {
    @Test
    fun testGetOrPut() {
        val cache = lruCache<String, String>(2)
        cache.put("key1", "value1")

        assertEquals("value1", cache.getOrPut("key1") { "value2" })
        assertEquals("value2", cache.getOrPut("key2") { "value2" })
        assertEquals("value1", cache["key1"])
        assertEquals("value2", cache["key2"])
    }
}
