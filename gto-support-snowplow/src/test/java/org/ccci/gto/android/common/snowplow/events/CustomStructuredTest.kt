package org.ccci.gto.android.common.snowplow.events

class CustomStructuredTest : CustomEventTest<CustomStructured>() {
    override fun event() = CustomStructured("category", "action")
}
