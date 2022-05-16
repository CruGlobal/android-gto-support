package org.ccci.gto.android.common.androidx.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import androidx.collection.LruCache;

import org.junit.Test;

import java.lang.ref.WeakReference;

public class WeakLruCacheTest {
    static final String KEY1 = "KEY1";
    static final String KEY2 = "KEY2";
    static final String VALUE1 = "VALUE1";
    static final String VALUE2 = "VALUE2";

    @Test
    public void testWeakDeletion() {
        final LruCache<String, String> cache = new WeakLruCache<>(1);

        // create Strings this way to prevent usage of intern table
        String val1 = new String(VALUE1);
        String val2 = new String(VALUE2);

        // populate the cache
        cache.put(KEY1, val1);
        assertEquals(1, cache.size());
        cache.put(KEY2, val2);
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
        final WeakLruCache<String, String> cache = new WeakLruCache<>(1);

        // populate the cache
        cache.put(KEY1, VALUE1);
        assertEquals(1, cache.size());
        assertEquals(VALUE1, cache.get(KEY1));

        // displace KEY1
        cache.put(KEY2, VALUE2);

        // set a new KEY1 value
        cache.put(KEY1, VALUE2);
        assertEquals(1, cache.size());

        // remove keys
        cache.remove(KEY1);
        cache.removeWeak(KEY2);
        assertEquals(0, cache.size());
        assertNull(cache.get(KEY1));
        assertNull(cache.get(KEY2));
    }

    static void forceGc() {
        Object obj = new Object();
        WeakReference ref = new WeakReference<>(obj);
        obj = null;
        while (ref.get() != null) {
            System.gc();
            try {
                Thread.sleep(100);
            } catch (final InterruptedException ignored) {
            }
        }
    }
}
