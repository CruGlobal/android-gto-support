package org.ccci.gto.android.common.snowplow.events

class CustomScreenViewTest : CustomEventTest<CustomScreenView>() {
    override fun event() = CustomScreenView("screen")
}
