package org.ccci.gto.android.common.support.v4.util;

import org.junit.Test;

import static org.ccci.gto.android.common.support.v4.util.WeakLruCacheTest.KEY1;
import static org.ccci.gto.android.common.support.v4.util.WeakLruCacheTest.KEY2;
import static org.ccci.gto.android.common.support.v4.util.WeakLruCacheTest.VALUE1;
import static org.ccci.gto.android.common.support.v4.util.WeakLruCacheTest.VALUE2;
import static org.ccci.gto.android.common.support.v4.util.WeakLruCacheTest.forceGc;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

public class WeakMultiKeyLruCacheTest {
    private static final String VALUE3 = "VALUE3";

    @Test
    public void testWeakDeletion() {
        final MultiKeyLruCache<String, String> cache = new WeakMultiKeyLruCache<>(1);

        // create Strings this way to prevent usage of intern table
        String val1 = new String(VALUE1);
        String val2 = new String(VALUE2);

        // populate the cache
        cache.putMulti(KEY1, val1);
        assertEquals(1, cache.size());
        cache.putMulti(KEY2, val2);
        assertEquals(1, cache.size());

        // controlled gc to make sure errors don't hide behind unscheduled GC's
        forceGc();

        // make sure both are still accessible
        assertEquals(VALUE1, cache.get(KEY1));
        assertEquals(VALUE2, cache.get(KEY2));

        // wipe local copy and force GC
        val1 = null;
        val2 = null;
        forceGc();

        // val1 should be missing now
        assertNull(cache.get(KEY1));
        assertNotEquals(VALUE1, cache.get(KEY1));
        assertEquals(VALUE2, cache.get(KEY2));
    }

    @Test
    public void verifyCacheConsistency() throws Exception {
        final MultiKeyLruCache<String, String> cache = new WeakMultiKeyLruCache<>(1);

        // populate the cache
        cache.put(KEY1, VALUE1);
        assertEquals(1, cache.size());
        assertEquals(VALUE1, cache.get(KEY1));

        // displace KEY1
        cache.put(KEY2, VALUE2);

        // set a new KEY1 value
        cache.put(KEY1, VALUE2);
        assertEquals(1, cache.size());

        // remove the key
        cache.remove(KEY1);
        assertEquals(0, cache.size());
        assertNull(cache.get(KEY1));
    }
}
