package org.ccci.gto.android.common.okta.oidc.clients.web

import android.app.Activity
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.app.ComponentActivity
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.okta.oidc.clients.BaseAuth
import com.okta.oidc.clients.web.SyncWebAuthClient
import java.util.concurrent.CountDownLatch
import kotlin.reflect.KClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
abstract class SyncWebAuthClientCoroutinesTest<A : Activity>(clazz: KClass<A>) {
    @get:Rule
    val activityScenario = ActivityScenarioRule(clazz.java)
    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    private val revokeAccessTokenLatch = CountDownLatch(1)
    private val revokeRefreshTokenLatch = CountDownLatch(1)
    private val signOutOfOktaLatch = CountDownLatch(1)

    private lateinit var client: SyncWebAuthClient

    @Before
    fun setup() {
        Dispatchers.setMain(TestCoroutineDispatcher())
        client = mock {
            var cancelled = false

            on { signOut(any(), any()) } doAnswer {
                revokeAccessTokenLatch.await()
                if (cancelled) return@doAnswer BaseAuth.FAILED_ALL

                revokeRefreshTokenLatch.await()
                if (cancelled) return@doAnswer BaseAuth.FAILED_ALL

                assertFalse(it.getArgument<Activity>(0).isFinishing)
                signOutOfOktaLatch.await()
                BaseAuth.SUCCESS
            }

            on { cancel() } doAnswer { cancelled = true }
        }
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Test(timeout = 5000)
    fun `signOutCoroutine()`() {
        activityScenario.scenario.onActivity {
            runBlocking {
                val signOut = async(Dispatchers.IO) { client.signOutCoroutine(it) }
                clearAllLatches()
                assertEquals(BaseAuth.SUCCESS, signOut.await())
                verify(client).signOut(any(), any())
                verifyNoMoreInteractions(client)
            }
        }
    }

    @Test(timeout = 5000)
    fun `signOutCoroutine() - Client cancelled`() {
        activityScenario.scenario.onActivity {
            runBlocking {
                launch(Dispatchers.IO) { client.signOutCoroutine(it) }
                delay(100)
                client.cancel()
                revokeAccessTokenLatch.countDown()
                verify(client).signOut(any(), any())
                verify(client).cancel()
                verifyNoMoreInteractions(client)
            }
        }
    }

    private fun clearAllLatches() {
        revokeAccessTokenLatch.countDown()
        revokeRefreshTokenLatch.countDown()
        signOutOfOktaLatch.countDown()
    }
}

class ActivitySyncWebAuthClientCoroutinesTest : SyncWebAuthClientCoroutinesTest<Activity>(Activity::class)
