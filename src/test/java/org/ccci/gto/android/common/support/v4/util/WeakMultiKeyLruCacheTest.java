package org.ccci.gto.android.common.support.v4.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import java.lang.ref.WeakReference;

public class WeakMultiKeyLruCacheTest {
    @Test
    public void testWeakDeletion() {
        final MultiKeyLruCache<String, String> cache = new WeakMultiKeyLruCache<>(1);

        // create Strings this way to prevent usage of intern table
        String val1 = new String("VALUE1");
        String val2 = new String("VALUE2");

        // populate the cache
        cache.putMulti("KEY1", val1);
        assertEquals(1, cache.size());
        cache.putMulti("KEY2", val2);
        assertEquals(1, cache.size());

        // controlled gc to make sure errors don't hide behind unscheduled GC's
        forceGc();

        // make sure both are still accessible
        assertEquals("VALUE1", cache.get("KEY1"));
        assertEquals("VALUE2", cache.get("KEY2"));

        // wipe local copy and force GC
        val1 = null;
        val2 = null;
        forceGc();

        // val1 should be missing now
        assertNull(cache.get("KEY1"));
        assertNotEquals("VALUE1", cache.get("KEY1"));
        assertEquals("VALUE2", cache.get("KEY2"));
    }

    private void forceGc() {
        Object obj = new Object();
        WeakReference ref = new WeakReference<Object>(obj);
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
