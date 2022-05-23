package org.ccci.gto.android.common.material.chip

import android.app.Activity
import android.content.Context
import androidx.appcompat.view.ContextThemeWrapper
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.robolectric.Robolectric
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [21])
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
    fun verifyElevationCompatSetsElevationDirectlyOnLollipop() {
        val chip: Chip = mock()
        chip.elevationCompat = 10f
        verify(chip).elevation = 10f
    }
}
