package org.ccci.gto.android.common.okta.authfoundation

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.okta.authfoundation.AuthFoundationDefaults
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.Config.NEWEST_SDK
import org.robolectric.annotation.Config.OLDEST_SDK

@RunWith(AndroidJUnit4::class)
class AuthFoundationDefaultsTest {
    @Test
    @Config(sdk = [OLDEST_SDK, Build.VERSION_CODES.N_MR1, Build.VERSION_CODES.O, NEWEST_SDK])
    fun testCompatClock() = runTest {
        AuthFoundationDefaults.enableClockCompat()
        assertEquals(System.currentTimeMillis() / 1000, AuthFoundationDefaults.clock.currentTimeEpochSecond())
    }
}
