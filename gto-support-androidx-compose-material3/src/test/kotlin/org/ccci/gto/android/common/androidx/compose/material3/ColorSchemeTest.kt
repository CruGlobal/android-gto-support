package org.ccci.gto.android.common.androidx.compose.material3

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ColorSchemeTest {
    @Test
    fun testIsLight() {
        assertTrue(lightColorScheme().isLight)
        assertFalse(darkColorScheme().isLight)
    }
}
