package org.ccci.gto.android.common.snowplow.events

class CustomStructuredTest : CustomEventTest<CustomStructured, CustomStructured.Builder>() {
    override fun event() = CustomStructured("category", "action")

    override fun eventBuilder() = CustomStructured.builder()
        .category("category")
        .action("action")

    override fun CustomStructured.Builder.buildEvent() = build()
}
