package org.ccci.gto.android.common.androidx.drawerlayout.widget

import android.view.MotionEvent
import androidx.drawerlayout.widget.DrawerLayout
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.RETURNS_DEEP_STUBS
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class HackyDrawerLayoutTest {
    private lateinit var layout: DrawerLayout
    private lateinit var motionEvent: MotionEvent

    @Before
    fun setup() {
        layout = HackyDrawerLayout(ApplicationProvider.getApplicationContext())
        motionEvent = mock(defaultAnswer = RETURNS_DEEP_STUBS)
    }

    @Test
    fun verifyOnTouchEventHandleNormal() {
        assertTrue(layout.onTouchEvent(motionEvent))
    }

    @Test
    fun verifyOnTouchEventHandleMalformedTouchEvent() {
        whenever(motionEvent.x).thenThrow(IllegalArgumentException("pointerIndex out of range"))
        assertTrue(layout.onTouchEvent(motionEvent))
    }

    @Test(expected = RuntimeException::class)
    fun verifyOnTouchEventPropagateOtherExceptions() {
        whenever(motionEvent.x).thenThrow(RuntimeException())
        layout.onTouchEvent(motionEvent)
        fail()
    }
}
