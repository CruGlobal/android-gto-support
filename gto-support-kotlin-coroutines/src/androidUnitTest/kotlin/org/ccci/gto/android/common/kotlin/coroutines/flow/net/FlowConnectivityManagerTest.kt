package org.ccci.gto.android.common.kotlin.coroutines.flow.net

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.core.content.getSystemService
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowConnectivityManager
import org.robolectric.shadows.ShadowNetworkInfo

@RunWith(AndroidJUnit4::class)
class FlowConnectivityManagerTest {
    private val context get() = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var shadowConnectivityManager: ShadowConnectivityManager

    @Before
    fun setup() {
        connectivityManager = requireNotNull(context.getSystemService())
        shadowConnectivityManager = shadowOf(connectivityManager)
    }

    @Test
    fun `isConnectedFlow()`() = runTest {
        // start off disconnected
        shadowConnectivityManager.setActiveNetworkInfo(
            ShadowNetworkInfo.newInstance(
                NetworkInfo.DetailedState.DISCONNECTED,
                ConnectivityManager.TYPE_WIFI,
                0,
                false,
                NetworkInfo.State.DISCONNECTED,
            ),
        )

        assertTrue(shadowConnectivityManager.networkCallbacks.isEmpty())
        context.isConnectedFlow().test {
            assertFalse(awaitItem())

            assertFalse(shadowConnectivityManager.networkCallbacks.isEmpty())
            val callback = shadowConnectivityManager.networkCallbacks.single()

            callback.onAvailable(mockk())
            assertTrue(awaitItem())

            callback.onLost(mockk())
            assertFalse(awaitItem())
        }
        assertTrue(shadowConnectivityManager.networkCallbacks.isEmpty())
    }
}
