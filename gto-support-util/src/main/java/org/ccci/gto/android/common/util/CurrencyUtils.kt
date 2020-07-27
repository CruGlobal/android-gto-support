package org.ccci.gto.android.common.util

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import org.ccci.gto.android.common.compat.util.LocaleCompat
import org.ccci.gto.android.common.compat.util.LocaleCompat.Category
import timber.log.Timber

@JvmOverloads
fun Double.formatCurrency(currency: String?, locale: Locale = LocaleCompat.getDefault(Category.FORMAT)) =
    formatCurrency(currency?.toCurrencyOrNull(), locale)

fun Double.formatCurrency(
    currency: Currency? = null,
    locale: Locale = LocaleCompat.getDefault(Category.FORMAT)
): String = NumberFormat.getCurrencyInstance(locale)
    .also {
        if (currency != null) {
            it.currency = currency
            it.minimumFractionDigits = currency.defaultFractionDigits
            it.maximumFractionDigits = currency.defaultFractionDigits
        } else if (it is DecimalFormat) {
            val symbols = it.decimalFormatSymbols
            symbols.currencySymbol = ""
            it.decimalFormatSymbols = symbols
        }
    }
    .format(this)

fun String.toCurrencyOrNull() = try {
    Currency.getInstance(this)
} catch (e: IllegalArgumentException) {
    Timber.tag("CurrencyUtils").e(e, "Unsupported currency: %s", this)
    null
}
