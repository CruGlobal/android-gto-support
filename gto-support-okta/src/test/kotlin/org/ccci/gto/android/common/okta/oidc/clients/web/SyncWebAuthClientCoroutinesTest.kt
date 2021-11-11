package org.ccci.gto.android.common.okta.oidc.clients.web

import android.app.Activity
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.okta.oidc.clients.BaseAuth
import com.okta.oidc.clients.web.SyncWebAuthClient
import java.util.concurrent.CountDownLatch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
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
    private val testScope = TestCoroutineScope(Job())

    @Before
    fun setup() {
        client = mock {
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

    @After
    fun cleanup() {
        assertEquals(0, testScope.coroutineContext.job.children.count())
        testScope.cleanupTestCoroutines()
    }

    @Test(timeout = 5000)
    fun `signOutSuspending()`() {
        activityScenario.scenario.onActivity {
            runBlocking {
                val signOut = async(Dispatchers.IO) { client.signOutSuspending(it) }
                clearAllLatches()
                assertEquals(BaseAuth.SUCCESS, signOut.await())
                assertTrue(signOutCompleted)
                verify(client).signOut(any(), any())
                verifyNoMoreInteractions(client)
            }
        }
    }

    @Test(timeout = 5000)
    fun `signOutSuspending() - Client cancelled`() {
        activityScenario.scenario.onActivity {
            runBlocking {
                val signOut = launch(Dispatchers.IO) { client.signOutSuspending(it) }
                signOutCalled.await()
                client.cancel()
                clearAllLatches()
                signOut.join()
                assertFalse(signOutCompleted)
                verify(client).signOut(any(), any())
                verify(client).cancel()
                verifyNoMoreInteractions(client)
            }
        }
    }

    @Test(timeout = 5000)
    fun `signOutSuspending() - Coroutine cancelled`() {
        activityScenario.scenario.onActivity { activity ->
            runBlocking {
                val signOut = testScope.launch(Dispatchers.IO) { client.signOutSuspending(activity) }
                signOutCalled.await()
                signOut.cancel()
                clearAllLatches()
                signOut.join()
                assertFalse(signOutCompleted)
                verify(client).signOut(any(), any())
                verify(client).cancel()
                verifyNoMoreInteractions(client)
            }
        }
    }

    @Test(timeout = 5000)
    fun `signOutSuspending() - Activity finished`() = with(activityScenario.scenario) {
        var signOut: Job? = null
        onActivity { signOut = testScope.launch(Dispatchers.IO) { client.signOutSuspending(it) } }
        signOutCalled.await()
        recreate()
        clearAllLatches()
        runBlocking { signOut!!.join() }
        assertFalse(signOutCompleted)
        verify(client).signOut(any(), any())
        verify(client).cancel()
        verifyNoMoreInteractions(client)
    }

    private fun clearAllLatches() {
        revokeTokensLatch.countDown()
        signOutOfOktaLatch.countDown()
    }
}
