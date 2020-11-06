package org.ccci.gto.android.common.snowplow.events

class CustomStructuredTest : CustomEventTest<CustomStructured.Builder>() {
    override fun eventBuilder() = CustomStructured.builder()
        .category("category")
        .action("action")

    override fun CustomStructured.Builder.buildEvent() = build()
}
