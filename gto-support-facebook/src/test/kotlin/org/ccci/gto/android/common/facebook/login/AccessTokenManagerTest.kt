package org.ccci.gto.android.common.facebook.login

import android.os.Looper
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.facebook.AccessToken
import com.facebook.AccessTokenManager
import com.facebook.FacebookSdk
import com.facebook.internal.Validate
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verifyAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class AccessTokenManagerTest {
    private lateinit var accessTokenManager: AccessTokenManager

    private val token1 = AccessToken("1", "1", "1", null, null, null, null, null, null, null)

    @Before
    fun setup() {
        mockkStatic(FacebookSdk::class, Validate::class)
        every { FacebookSdk.getApplicationContext() } returns ApplicationProvider.getApplicationContext()
        every { Validate.sdkInitialized() } just Runs

        accessTokenManager = AccessTokenManager.getInstance()
    }

    // region currentAccessTokenFlow()
    @Test
    fun `currentAccessTokenFlow()`() = runTest {
        accessTokenManager.currentAccessTokenFlow().test {
            assertNull(awaitItem())

            accessTokenManager.currentAccessToken = token1
            executePendingBroadcasts()
            assertEquals(token1, awaitItem())

            accessTokenManager.currentAccessToken = null
            executePendingBroadcasts()
            assertNull(awaitItem())
        }
    }

    @Test
    fun `currentAccessTokenFlow() - first item is current token`() = runTest {
        accessTokenManager.currentAccessToken = token1
        executePendingBroadcasts()
        assertEquals(token1, accessTokenManager.currentAccessTokenFlow().first())
    }

    @Test
    fun `currentAccessTokenFlow() - Flow correctly starts & stops tracking`() = runTest {
        val localBroadcastManager: LocalBroadcastManager = mockk {
            every { registerReceiver(any(), any()) } just Runs
            every { unregisterReceiver(any()) } just Runs
        }
        mockkStatic(LocalBroadcastManager::class) {
            every { LocalBroadcastManager.getInstance(any()) } returns localBroadcastManager

            accessTokenManager.currentAccessTokenFlow().test {
                awaitItem()
                verifyAll { localBroadcastManager.registerReceiver(any(), any()) }
            }
            verifyAll {
                localBroadcastManager.registerReceiver(any(), any())
                localBroadcastManager.unregisterReceiver(any())
            }
        }
    }
    // endregion currentAccessTokenFlow()

    private fun executePendingBroadcasts() = shadowOf(Looper.getMainLooper()).idle()
}
