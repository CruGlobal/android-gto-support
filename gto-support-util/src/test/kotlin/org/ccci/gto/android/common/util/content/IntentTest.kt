package org.ccci.gto.android.common.util.content

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IntentTest {
    @Test
    fun `intentEquals()`() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://example.com"))
            .putExtra("key", "value")
        val intent2 = Intent(ApplicationProvider.getApplicationContext(), Activity::class.java)

        assertTrue(intent equalsIntent Intent(intent))
        assertTrue(intent2 equalsIntent Intent(intent2))

        assertFalse(intent equalsIntent Intent(intent).apply { action = null })
        assertFalse(intent equalsIntent Intent(intent).apply { data = null })
        assertFalse(intent equalsIntent Intent(intent).apply { removeExtra("key") })
        assertFalse(intent2 equalsIntent Intent(intent2).apply { component = null })
    }
}
