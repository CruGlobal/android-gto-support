package org.ccci.gto.android.common.util

import java.util.Currency
import java.util.Locale
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.junit.runner.RunWith

@RunWith(JUnitParamsRunner::class)
class CurrencyTest {
    // region formatCurrency() Tests
    @Test
    @Parameters(method = "formatCurrencyTests")
    fun verifyFormatCurrency(amount: Double, locale: Locale, currency: String?, expected: String) {
        assertEquals(expected, amount.formatCurrency(currency, locale))
    }

    fun formatCurrencyTests() = arrayOf(
        arrayOf(1.2345, Locale.US, "USD", "$1.23"),
        arrayOf(1.2345, Locale.CANADA, "CAD", "$1.23"),
        arrayOf(1.2345, Locale.CANADA, "USD", "US$1.23"),
        arrayOf(1.2345, Locale.US, "CAD", "CA$1.23"),
        arrayOf(1.2345, Locale("es", "ES"), "USD", "1,23 US\$"),
        arrayOf(1.2345, Locale.US, "JPY", "¥1"),
        arrayOf(1000.21, Locale.GERMANY, "EUR", "1.000,21 €"),
        arrayOf(1000.21, Locale("en", "DE"), "EUR", "1.000,21 €"),
        arrayOf(1.23, Locale.US, null, "1.23")
    )
    // endregion formatCurrency() Tests

    // region toCurrencyOrNull()
    @Test
    fun verifyToCurrencyOrNull() {
        assertEquals(Currency.getInstance(Locale.US), "USD".toCurrencyOrNull())
        assertNull("aaa".toCurrencyOrNull())
        assertNull("2384".toCurrencyOrNull())
    }
    // endregion toCurrencyOrNull()
}
