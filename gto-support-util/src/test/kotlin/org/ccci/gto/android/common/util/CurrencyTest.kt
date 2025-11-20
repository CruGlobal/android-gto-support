package org.ccci.gto.android.common.util

import java.util.Currency
import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CurrencyTest {
    // region toCurrencyOrNull()
    @Test
    fun verifyToCurrencyOrNull() {
        assertEquals(Currency.getInstance(Locale.US), "USD".toCurrencyOrNull())
        assertNull("aaa".toCurrencyOrNull())
        assertNull("2384".toCurrencyOrNull())
    }
    // endregion toCurrencyOrNull()
}
