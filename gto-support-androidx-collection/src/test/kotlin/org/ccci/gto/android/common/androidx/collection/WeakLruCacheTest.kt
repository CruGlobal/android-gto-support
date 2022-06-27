package org.ccci.gto.android.common.androidx.collection

import java.lang.ref.WeakReference
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

private const val KEY1 = "KEY1"
private const val KEY2 = "KEY2"
private const val VALUE1 = "VALUE1"
private const val VALUE2 = "VALUE2"

class WeakLruCacheTest {
    @Test
    fun testWeakDeletion() {
        val cache = WeakLruCache<String, CacheValue>(1)

        var val1: CacheValue? = CacheValue(VALUE1)
        var val2: CacheValue? = CacheValue(VALUE2)

        // populate the cache
        cache.put(KEY1, val1!!)
        assertEquals(1, cache.size())
        cache.put(KEY2, val2!!)
        assertEquals(1, cache.size())

        // controlled gc to make sure errors don't hide behind unscheduled GC's
        forceGc()

        // make sure both are still accessible
        assertEquals(CacheValue(VALUE1), cache[KEY1])
        assertEquals(CacheValue(VALUE2), cache[KEY2])

        // wipe local copy and force GC
        val1 = null
        val2 = null
        forceGc()

        // val1 should be missing now
        assertNull(cache[KEY1])
        assertNotEquals(CacheValue(VALUE1), cache[KEY1])
        assertEquals(CacheValue(VALUE2), cache[KEY2])
    }

    @Test
    fun verifyCacheConsistency() {
        val cache = WeakLruCache<String, String>(1)

        // populate the cache
        cache.put(KEY1, VALUE1)
        assertEquals(1, cache.size())
        assertEquals(VALUE1, cache[KEY1])

        // displace KEY1
        cache.put(KEY2, VALUE2)

        // set a new KEY1 value
        cache.put(KEY1, VALUE2)
        assertEquals(1, cache.size())

        // remove keys
        cache.removeWeak(KEY1)
        cache.removeWeak(KEY2)
        assertEquals(0, cache.size())
        assertNull(cache[KEY1])
        assertNull(cache[KEY2])
    }

    @Test
    fun verifyWeakLruCacheCreate() {
        val cache = weakLruCache<String, CacheValue>(1, create = { CacheValue(it) })

        // ensure created values are persisted via weak cache
        val value1: CacheValue? = cache.get(KEY1)
        assertEquals(1, cache.size())
        var value2: CacheValue? = cache.get(KEY2)
        assertEquals(1, cache.size())

        // restore value1 from internal weak map
        assertSame(value1, cache.get(KEY1))

        // value2 is in the weak cache, allow it to be garbage collected
        val value2Ref = WeakReference(value2)
        assertNotNull(value2Ref.get())
        value2 = null
        forceGc()
        assertNull(value2Ref.get())
    }
}

private data class CacheValue(val str: String)

private fun forceGc() {
    var obj: Any? = Any()
    val ref = WeakReference(obj)
    obj = null
    while (ref.get() != null) {
        System.gc()
        try {
            Thread.sleep(100)
        } catch (ignored: InterruptedException) {
        }
    }
}
