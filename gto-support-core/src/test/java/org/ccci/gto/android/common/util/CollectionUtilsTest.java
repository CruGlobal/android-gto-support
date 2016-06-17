package org.ccci.gto.android.common.util;

import com.google.common.collect.ImmutableList;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.ccci.gto.android.common.util.CollectionUtils.newCollection;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CollectionUtilsTest {
    @Test
    public void verifyNewCollectionNullType() throws Exception {
        assertNull(newCollection(null));
    }

    @Test
    public void verifyNewCollectionSupported() throws Exception {
        for (final Class<?> type : ImmutableList
                .of(Collection.class, List.class, ArrayList.class, LinkedList.class, Set.class, HashSet.class)) {
            assertTrue(type.isInstance(newCollection(type)));
        }
    }
}
