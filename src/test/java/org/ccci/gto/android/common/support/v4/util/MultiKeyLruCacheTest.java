package org.ccci.gto.android.common.support.v4.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class MultiKeyLruCacheTest {
    @Test
    public void testNormalUsage() throws Exception {
        final MultiKeyLruCache<String, String> cache = new MultiKeyLruCache<>(1);
        final String sharedVal = "common value";

        // populate cache
        for (int i = 0; i < 100; i++) {
            cache.putMulti("KEY" + Integer.toString(i), sharedVal);
        }

        // test cache
        assertEquals(100, cache.size());
        for (int i = 0; i < 100; i++) {
            assertEquals(sharedVal, cache.get("KEY" + Integer.toString(i)));
        }

        // force eviction
        cache.putMulti("OTHERKEY", "new val");

        // test cache
        assertNotNull(cache.get("OTHERKEY"));
        assertEquals(1, cache.size());
        for (int i = 0; i < 100; i++) {
            assertNull(cache.get("KEY" + Integer.toString(i)));
        }
    }
}
