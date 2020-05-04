package org.ccci.gto.android.common.scarlet.actioncable.model

import com.squareup.moshi.Moshi
import net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson
import org.junit.Test

class IdentifierTest {
    @Test
    fun testSerialization() {
        val moshi = Moshi.Builder().build().adapter(Identifier::class.java)
        assertThatJson(moshi.toJson(Identifier("channel"))).isEqualTo("{channel:\"channel\"}")
    }
}
