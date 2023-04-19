package org.ccci.gto.android.common.jsonapi.util;

import static org.ccci.gto.android.common.jsonapi.util.CollectionUtils.newCollection;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("KotlinInternalInJava")
public class CollectionUtilsTest {
    @Test
    public void verifyNewCollectionSupported() {
        for (final Class<? extends Collection> type : Arrays
                .asList(Collection.class, List.class, ArrayList.class, LinkedList.class, Set.class, HashSet.class)) {
            assertTrue(type.isInstance(newCollection(type)));
        }
    }
}
