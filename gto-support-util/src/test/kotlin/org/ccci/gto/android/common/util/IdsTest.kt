package org.ccci.gto.android.common.util

import androidx.test.ext.junit.runners.AndroidJUnit4
import java.lang.StringBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IdsTest {
    @Test
    fun `generate reuses ids for equal objects`() {
        val str1 = "a"
        val str2 = StringBuilder().append('a').toString()
        assertEquals(str1, str2)
        assertNotSame(str1, str2)

        val id1 = Ids.generate(str1)
        val id2 = Ids.generate(str2)
        assertEquals(id1, id2)
    }

    @Test
    fun `verify looks up previously generated ids`() {
        val id = Ids.generate("a")
        val str = Ids.lookup<String>(id)
        assertEquals("a", str)
    }
}
