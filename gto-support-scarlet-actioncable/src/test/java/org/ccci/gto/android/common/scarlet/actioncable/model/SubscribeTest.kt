package org.ccci.gto.android.common.scarlet.actioncable.model

import com.squareup.moshi.Moshi
import net.javacrumbs.jsonunit.assertj.assertThatJson
import org.ccci.gto.android.common.moshi.adapter.StringifyJsonAdapterFactory
import org.json.JSONObject
import org.junit.Test

class SubscribeTest {
    @Test
    fun testSerialization() {
        val moshi = Moshi.Builder()
            .add(StringifyJsonAdapterFactory)
            .add(IdentifierJsonAdapter)
            .build()
        val adapter = moshi.adapter(Subscribe::class.java)
        val json = adapter.toJson(Subscribe("channel"))
        assertThatJson(json).node("command").isEqualTo("subscribe")
        assertThatJson(JSONObject(json).getString("identifier")).isEqualTo("{channel:\"channel\"}")
    }
}
