package org.ccci.gto.android.common.compat.content

import android.content.Intent
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.io.Serializable
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

private const val KEY1 = "key1"
private const val KEY2 = "key2"

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Config.OLDEST_SDK, Build.VERSION_CODES.S_V2/*, Build.VERSION_CODES.TIRAMISU*/, Config.NEWEST_SDK])
class IntentCompatTest {
    @Test
    fun testGetSerializableExtraCompat() {
        val intent = Intent()
        intent.putExtra(KEY1, A())

        assertNotNull(intent.getSerializableExtraCompat(KEY1, A::class.java))
        assertNull(intent.getSerializableExtraCompat(KEY1, String::class.java))
        assertNull(intent.getSerializableExtraCompat(KEY2, A::class.java))
    }

    private class A : Serializable
}
