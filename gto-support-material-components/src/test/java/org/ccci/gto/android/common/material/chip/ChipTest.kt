package org.ccci.gto.android.common.material.chip

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.appcompat.view.ContextThemeWrapper
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import org.ccci.gto.android.common.material.components.R
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.hamcrest.Matchers.lessThan
import org.junit.Assert.assertEquals
import org.junit.Assume.assumeThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [19, 21])
class ChipTest {
    private lateinit var context: Context

    @Before
    fun setupContext() {
        val activity = Robolectric.buildActivity(Activity::class.java).get()
        context = ContextThemeWrapper(activity, R.style.Theme_MaterialComponents)
    }

    @Test
    fun verifyElevationCompatRoundtrip() {
        val chip = Chip(context)

        chip.elevationCompat = 0f
        assertEquals(0f, chip.elevationCompat, 0.0001f)
        assertEquals(0f, (chip.chipDrawable as ChipDrawable).elevation)
        chip.elevationCompat = 10f
        assertEquals(10f, chip.elevationCompat, 0.0001f)
        assertEquals(10f, (chip.chipDrawable as ChipDrawable).elevation)
    }

    @Test
    fun verifyElevationCompatUsesChipDrawableBeforeLollipop() {
        assumePreLollipop()

        val drawable: ChipDrawable = mock()
        val chip: Chip = mock { on { chipDrawable } doReturn drawable }

        chip.elevationCompat = 10f
        verify(chip, never()).elevation = any()
        verify(drawable).elevation = 10f
    }

    @Test
    fun verifyElevationCompatSetsElevationDirectlyOnLollipop() {
        assumeLollipop()

        val chip: Chip = mock()
        chip.elevationCompat = 10f
        verify(chip).elevation = 10f
    }
}

private fun assumeLollipop() = assumeThat(Build.VERSION.SDK_INT, greaterThanOrEqualTo(Build.VERSION_CODES.LOLLIPOP))
private fun assumePreLollipop() = assumeThat(Build.VERSION.SDK_INT, lessThan(Build.VERSION_CODES.LOLLIPOP))
