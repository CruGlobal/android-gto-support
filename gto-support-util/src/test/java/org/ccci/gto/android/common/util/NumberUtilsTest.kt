package org.ccci.gto.android.common.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Locale

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
}
