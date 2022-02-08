package org.ccci.gto.android.common.okta.oidc.clients.web

import android.app.Activity
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.okta.oidc.clients.BaseAuth
import com.okta.oidc.clients.sessions.SyncSessionClient
import com.okta.oidc.clients.web.SyncWebAuthClient
import java.util.concurrent.CountDownLatch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class SyncWebAuthClientCoroutinesTest {
    @get:Rule
    val activityScenario = ActivityScenarioRule(Activity::class.java)

    private val signOutCalled = CountDownLatch(1)
    private val revokeTokensLatch = CountDownLatch(1)
    private val signOutOfOktaLatch = CountDownLatch(1)
    private var signOutCompleted = false

    private lateinit var client: SyncWebAuthClient
    private val sessionClient = mock<SyncSessionClient>()

    @Before
    fun setup() {
        client = mock {
            on { sessionClient } doReturn sessionClient

            var cancelled = false

            on { signOut(any(), any()) } doAnswer {
                signOutCalled.countDown()
                revokeTokensLatch.await()
                if (cancelled) return@doAnswer BaseAuth.FAILED_ALL

                assertFalse(it.getArgument<Activity>(0).isFinishing)
                signOutOfOktaLatch.await()
                signOutCompleted = true
                BaseAuth.SUCCESS
            }

            on { cancel() } doAnswer { cancelled = true }
        }
    }

    @Test(timeout = 5000)
    fun `signOutSuspending()`() {
        activityScenario.scenario.onActivity {
            runTest {
                val signOut = async { client.signOutSuspending(it) }
                clearAllLatches()
                assertEquals(BaseAuth.SUCCESS, signOut.await())
                assertTrue(signOutCompleted)
                verify(client).signOut(any(), any())
                verify(client).sessionClient
                verify(sessionClient).clear()
                verifyNoMoreInteractions(client)
            }
        }
    }

    @Test(timeout = 5000)
    fun `signOutSuspending() - Don't REMOVE_TOKENS`() {
        activityScenario.scenario.onActivity {
            runTest {
                val signOut = async { client.signOutSuspending(it, BaseAuth.SIGN_OUT_SESSION) }
                clearAllLatches()
                assertEquals(BaseAuth.SUCCESS, signOut.await())
                assertTrue(signOutCompleted)
                verify(client).signOut(any(), any())
                verify(sessionClient, never()).clear()
                verifyNoMoreInteractions(client)
            }
        }
    }

    @Test(timeout = 5000)
    fun `signOutSuspending() - Client cancelled`() {
        activityScenario.scenario.onActivity {
            runTest {
                val signOut = launch(UnconfinedTestDispatcher()) { client.signOutSuspending(it) }
                @Suppress("BlockingMethodInNonBlockingContext")
                signOutCalled.await()
                client.cancel()
                clearAllLatches()
                signOut.join()
                assertFalse(signOutCompleted)
                verify(client).signOut(any(), any())
                verify(client).cancel()
                verify(client).sessionClient
                verify(sessionClient).clear()
                verifyNoMoreInteractions(client)
            }
        }
    }

    @Test(timeout = 5000)
    fun `signOutSuspending() - Coroutine cancelled`() {
        activityScenario.scenario.onActivity { activity ->
            runTest {
                val signOut = launch(UnconfinedTestDispatcher()) { client.signOutSuspending(activity) }
                @Suppress("BlockingMethodInNonBlockingContext")
                signOutCalled.await()
                signOut.cancel()
                clearAllLatches()
                signOut.join()
                assertFalse(signOutCompleted)
                verify(client).signOut(any(), any())
                verify(client).cancel()
                verify(client).sessionClient
                verify(sessionClient).clear()
                verifyNoMoreInteractions(client)
            }
        }
    }

    @Test(timeout = 5000)
    fun `signOutSuspending() - Activity finished`() = with(activityScenario.scenario) {
        runTest {
            lateinit var signOut: Job
            onActivity { signOut = launch(UnconfinedTestDispatcher()) { client.signOutSuspending(it) } }
            @Suppress("BlockingMethodInNonBlockingContext")
            signOutCalled.await()
            recreate()
            clearAllLatches()
            signOut.join()
            assertFalse(signOutCompleted)
            verify(client).signOut(any(), any())
            verify(client).cancel()
            verify(client).sessionClient
            verify(sessionClient).clear()
            verifyNoMoreInteractions(client)
        }
    }

    private fun clearAllLatches() {
        revokeTokensLatch.countDown()
        signOutOfOktaLatch.countDown()
    }
}
