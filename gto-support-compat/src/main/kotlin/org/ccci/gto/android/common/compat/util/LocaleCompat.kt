package org.ccci.gto.android.common.compat.util

import android.annotation.TargetApi
import android.os.Build
import java.util.Locale

object LocaleCompat {
    private val COMPAT: LocaleCompatMethods = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> NougatLocaleCompatMethods()
        else -> BaseLocaleCompatMethods()
    }

    @JvmStatic
    fun getDefault(category: Category) = COMPAT.getDefault(category)

    enum class Category { DISPLAY, FORMAT }
}

private sealed class LocaleCompatMethods {
    abstract fun getDefault(category: LocaleCompat.Category): Locale
}

private open class BaseLocaleCompatMethods : LocaleCompatMethods() {
    override fun getDefault(category: LocaleCompat.Category): Locale = Locale.getDefault()
}

@TargetApi(Build.VERSION_CODES.N)
private class NougatLocaleCompatMethods : BaseLocaleCompatMethods() {
    override fun getDefault(category: LocaleCompat.Category): Locale = Locale.getDefault(
        when (category) {
            LocaleCompat.Category.DISPLAY -> Locale.Category.DISPLAY
            LocaleCompat.Category.FORMAT -> Locale.Category.FORMAT
        }
    )
}
