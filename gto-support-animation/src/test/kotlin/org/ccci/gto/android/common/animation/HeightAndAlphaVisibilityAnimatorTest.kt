package org.ccci.gto.android.common.animation

import android.animation.TimeInterpolator
import android.view.View
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.fail
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub

class HeightAndAlphaVisibilityAnimatorTest {
    private lateinit var view: View

    @Before
    fun setup() {
        view = mock()
    }

    @Test
    fun verifyInitBlockOnlyRunsOnInitialCreation() {
        val d = Random.nextLong(0, Long.MAX_VALUE)
        val i = mock<TimeInterpolator>()
        val animator = HeightAndAlphaVisibilityAnimator.of(view) {
            duration = d
            interpolator = i
        }
        assertEquals(d, animator.duration)
        assertSame(i, animator.interpolator)

        view.stub { on { getTag(R.id.gto_height_alpha_animator) } doReturn animator }
        val animator2 = HeightAndAlphaVisibilityAnimator.of(view) { fail("onInit() shouldn't run for reused animator") }
        assertSame(animator, animator2)
    }
}
