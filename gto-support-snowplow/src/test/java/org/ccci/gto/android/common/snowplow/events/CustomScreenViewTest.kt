package org.ccci.gto.android.common.snowplow.events

class CustomScreenViewTest : CustomEventTest<CustomScreenView, CustomScreenView.Builder>() {
    override fun event() = CustomScreenView("screen")

    override fun eventBuilder() = CustomScreenView.builder().name("screen")
    override fun CustomScreenView.Builder.buildEvent() = build()
}
