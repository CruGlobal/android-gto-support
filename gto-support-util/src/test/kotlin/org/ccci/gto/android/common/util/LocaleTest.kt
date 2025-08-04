package org.ccci.gto.android.common.util

import java.util.Locale
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.junit.Test

class LocaleTest {
    @Test
    fun verifyGetOptionalDisplayNameExists() {
        assertNotNull(Locale.ENGLISH.getOptionalDisplayName())
        assertNotNull(Locale.ENGLISH.getOptionalDisplayName(null))
        assertEquals("English", Locale.ENGLISH.getOptionalDisplayName(Locale.ENGLISH))
        assertNotNull(Locale.FRENCH.getOptionalDisplayName())
        assertNotNull(Locale.FRENCH.getOptionalDisplayName(null))
        assertEquals("français", Locale.FRENCH.getOptionalDisplayName(Locale.FRENCH))
    }

    @Test
    fun verifyGetOptionalDisplayNameDoesntExist() {
        assertNull(Locale("x").getOptionalDisplayName(Locale.ENGLISH))
        assertNull(Locale("x").getOptionalDisplayName(null))
        assertNull(Locale("x").getOptionalDisplayName())
    }

    @Test
    fun testToLocale() {
        assertEquals(Locale.ENGLISH, "en".toLocale())
    }

    @Test
    fun verifyBuildOrNull() {
        val builder = Locale.Builder()
        assertNull(builder.buildOrNull())
        builder.setLocale(Locale.ENGLISH)
        assertEquals(Locale.ENGLISH, builder.buildOrNull())
        builder.setLanguage(null)
        assertNull(builder.buildOrNull())
    }
}
