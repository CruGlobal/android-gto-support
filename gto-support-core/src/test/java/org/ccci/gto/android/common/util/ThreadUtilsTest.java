package org.ccci.gto.android.common.util;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ThreadUtilsTest {
    @Test
    public void testGetLockGenericKey() throws Exception {
        final Map<GenericKey, Object> locks = new HashMap<>();

        // generate keys needed for testing
        final Object obj1 = new Object();
        final Object obj2 = new Object();
        final GenericKey key1a = new GenericKey("a", 1, obj1);
        final GenericKey key1b = new GenericKey("a", 1, obj1);
        final GenericKey key2 = new GenericKey("a", 1, obj2);

        assertEquals(ThreadUtils.getLock(locks, key1a), ThreadUtils.getLock(locks, key1a));
        assertEquals(ThreadUtils.getLock(locks, key1a), ThreadUtils.getLock(locks, key1b));
        assertNotEquals(ThreadUtils.getLock(locks, key1a), ThreadUtils.getLock(locks, key2));
        assertEquals(ThreadUtils.getLock(locks, key1b), ThreadUtils.getLock(locks, key1b));
        assertNotEquals(ThreadUtils.getLock(locks, key1b), ThreadUtils.getLock(locks, key2));
        assertEquals(ThreadUtils.getLock(locks, key2), ThreadUtils.getLock(locks, key2));
    }
}
