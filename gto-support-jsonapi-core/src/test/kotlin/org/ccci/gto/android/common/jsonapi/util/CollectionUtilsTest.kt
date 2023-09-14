package org.ccci.gto.android.common.jsonapi.util

import kotlin.test.assertTrue
import org.junit.Test

class CollectionUtilsTest {
    @Test
    fun verifyNewCollectionSupported() {
        for (type in listOf(
            Collection::class.java,
            List::class.java,
            ArrayList::class.java,
            Set::class.java,
            HashSet::class.java,
            LinkedHashSet::class.java,
        )) {
            assertTrue(type.isInstance(newCollection(type)))
        }
    }
}
