package org.ccci.gto.android.common.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Locale

class LocaleTest {
    @Test
    fun verifyGetOptionalDisplayNameExists() {
        assertNotNull(LocaleUtils.getOptionalDisplayName(Locale.ENGLISH, null))
        assertEquals("English", LocaleUtils.getOptionalDisplayName(Locale.ENGLISH, Locale.ENGLISH))
        assertNotNull(LocaleUtils.getOptionalDisplayName(Locale.FRENCH, null))
        assertEquals("français", LocaleUtils.getOptionalDisplayName(Locale.FRENCH, Locale.FRENCH))
    }

    @Test
    fun verifyGetOptionalDisplayNameDoesntExist() {
        assertNull(LocaleUtils.getOptionalDisplayName(Locale("x"), Locale.ENGLISH))
        assertNull(LocaleUtils.getOptionalDisplayName(Locale("x"), null))
    }

    @Test
    fun testToLocale() {
        assertEquals(Locale.ENGLISH, "en".toLocale())
    }
}
