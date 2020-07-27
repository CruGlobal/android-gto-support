package org.ccci.gto.android.common.util

import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

private val LOCALE_SPAIN = Locale("es", "ES")

class NumberUtilsTest {
    @Test
    fun verifyLocalizedToDoubleOrNull() {
        assertEquals(1.12, "1.12".localizedToDoubleOrNull(Locale.US)!!, 0.000001)
        assertEquals(1000.12, "1000.12".localizedToDoubleOrNull(Locale.US)!!, 0.000001)
        assertEquals(1000.12, "1,000.12".localizedToDoubleOrNull(Locale.US)!!, 0.000001)
        assertEquals(1000.12, "  1,000.12".localizedToDoubleOrNull(Locale.US)!!, 0.000001)
        assertEquals(1000.12, "  1,000.12  ".localizedToDoubleOrNull(Locale.US)!!, 0.000001)
        assertEquals(1000.12, "1.000,12".localizedToDoubleOrNull(LOCALE_SPAIN)!!, 0.000001)
    }

    @Test
    fun verifyLocalizedToDoubleOrNullInvalid() {
        assertNull("1.000,12".localizedToDoubleOrNull(Locale.US))
        assertNull("".localizedToDoubleOrNull(Locale.US))
        assertNull(" a ".localizedToDoubleOrNull(Locale.US))
        assertNull("1,000.12".localizedToDoubleOrNull(LOCALE_SPAIN))
    }

    @Test
    fun testNumberFormat() {
        assertEquals("1.123", 1.123f.format(Locale.US))
        assertEquals("1,123", 1.123f.format(LOCALE_SPAIN))
        assertEquals("1.12", 1.123f.format(locale = Locale.US, maximumFractionDigits = 2))
        assertEquals("1,12", 1.123f.format(locale = LOCALE_SPAIN, maximumFractionDigits = 2))
    }
}
