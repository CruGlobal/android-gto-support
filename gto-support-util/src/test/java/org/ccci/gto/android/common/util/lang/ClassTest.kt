package org.ccci.gto.android.common.util.lang

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ClassTest {
    @Test
    fun verifyGetClassOrNull() {
        assertNull(getClassOrNull("org.ccci.gto.android.common.util.lang.ClassTest\$Invalid"))
        assertEquals(Test1::class.java, getClassOrNull("org.ccci.gto.android.common.util.lang.ClassTest\$Test1"))
    }

    @Test
    fun verifyGetMethod() {
        assertNull(Test1::class.java.getDeclaredMethodOrNull("invalid"))
        assertEquals(
            Test1::class.java.getDeclaredMethod("method1"),
            Test1::class.java.getDeclaredMethodOrNull("method1")
        )
        assertNull(Test1::class.java.getDeclaredMethodOrNull("method1", String::class.java))
        assertEquals(
            Test1::class.java.getDeclaredMethod("method2", String::class.java),
            Test1::class.java.getDeclaredMethodOrNull("method2", String::class.java)
        )
        assertNull(Test1::class.java.getDeclaredMethodOrNull("method2"))
        assertNull(Test1::class.java.getDeclaredMethodOrNull("method2", Int::class.java))
    }

    class Test1 {
        private fun method1() = Unit
        private fun method2(param: String) = Unit
    }
}
