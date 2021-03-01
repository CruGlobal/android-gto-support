package org.ccci.gto.android.common.animation

import android.animation.TimeInterpolator
import android.view.View
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.stub
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

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

        view.stub { on { getTag(R.id.gto_height_alpha_animator)} doReturn animator }
        val animator2 = HeightAndAlphaVisibilityAnimator.of(view) { fail("onInit() block shouldn't run on reused animator") }
        assertSame(animator, animator2)
    }
}
