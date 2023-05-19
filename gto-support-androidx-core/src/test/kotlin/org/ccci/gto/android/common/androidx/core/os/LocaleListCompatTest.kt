package org.ccci.gto.android.common.androidx.core.os

import androidx.core.os.LocaleListCompat
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test

class LocaleListCompatTest {
    @Test
    fun `asIterable()`() {
        assertEquals(emptyList<Locale>(), LocaleListCompat.getEmptyLocaleList().asIterable().toList())
        assertEquals(
            listOf(Locale.ENGLISH, Locale.FRENCH),
            LocaleListCompat.forLanguageTags("en,fr").asIterable().toList(),
        )
    }
}
