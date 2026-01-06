package org.ccci.gto.android.common.scarlet.actioncable.model

import com.squareup.moshi.Moshi
import net.javacrumbs.jsonunit.assertj.assertThatJson
import org.junit.Test

class IdentifierTest {
    private val moshi = Moshi.Builder()
        .add(IdentifierJsonAdapter)
        .build().adapter(Identifier::class.java)

    @Test
    fun testSerialization() {
        assertThatJson(moshi.toJson(Identifier("channel"))).isEqualTo("{channel:\"channel\"}")
    }

    @Test
    fun testSerializationWithAttributes() {
        assertThatJson(moshi.toJson(Identifier("channel", mapOf("attr" to 1))))
            .isEqualTo("""{channel:"channel",attr:1}""")
    }
}
