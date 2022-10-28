package org.ccci.gto.support.fluidsonic.locale

import io.fluidsonic.locale.Locale
import kotlin.test.Test
import kotlin.test.assertEquals

class PlatformLocaleTest {
    @Test
    fun testRoundtripping() {
        val locale = Locale.forLanguageTag("en-us")
        assertEquals(locale, locale.toPlatform().toCommon())
    }
}
