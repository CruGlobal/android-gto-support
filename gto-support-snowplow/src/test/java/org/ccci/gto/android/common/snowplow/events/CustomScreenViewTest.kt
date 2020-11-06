package org.ccci.gto.android.common.snowplow.events

class CustomScreenViewTest : CustomEventTest<CustomScreenView.Builder>() {
    override fun eventBuilder() = CustomScreenView.builder()
    override fun CustomScreenView.Builder.buildEvent() = build()
}
