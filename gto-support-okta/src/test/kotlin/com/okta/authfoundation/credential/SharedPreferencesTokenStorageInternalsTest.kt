package com.okta.authfoundation.credential

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.okta.authfoundation.InternalAuthFoundationApi
import com.okta.authfoundation.client.OidcClient
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlinx.coroutines.Dispatchers
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(InternalAuthFoundationApi::class)
class SharedPreferencesTokenStorageInternalsTest {
    private val context: Context get() = ApplicationProvider.getApplicationContext()
    private val oidcClient: OidcClient = mockk {
        every { configuration } returns mockk {
            every { json } returns mockk()
            every { ioDispatcher } returns Dispatchers.Unconfined
            every { eventCoordinator } returns mockk(relaxUnitFun = true)
        }
    }
    private val keySpec = MasterKeys.AES256_GCM_SPEC
    private val sharedPreferences: SharedPreferences = mockk()

    @BeforeTest
    fun setup() {
        mockkStatic(MasterKeys::class, EncryptedSharedPreferences::class)
        every { MasterKeys.getOrCreate(any()) } returns "key"
        every { EncryptedSharedPreferences.create(any<String>(), any(), any(), any(), any()) } returns sharedPreferences
    }

    @AfterTest
    fun cleanup() {
        unmockkStatic(MasterKeys::class, EncryptedSharedPreferences::class)
    }

    @Test
    fun `create()`() {
        assertNotNull(SharedPreferencesTokenStorageInternals.create(oidcClient, context, keySpec))
    }

    // region getSharedPreferences()
    @Test
    fun `getSharedPreferences()`() {
        val preferences = SharedPreferencesTokenStorageInternals.create(oidcClient, context, keySpec)
        assertEquals(sharedPreferences, SharedPreferencesTokenStorageInternals.getSharedPreferences(preferences))
    }

    class SharedPreferencesException : Exception()

    @Test
    fun `getSharedPreferences() - Propagates Exceptions`() {
        val preferences = SharedPreferencesTokenStorageInternals.create(oidcClient, context, keySpec)
        every { MasterKeys.getOrCreate(any()) } throws SharedPreferencesException()
        assertFailsWith<SharedPreferencesException> {
            SharedPreferencesTokenStorageInternals.getSharedPreferences(preferences)
        }
    }
    // endregion getSharedPreferences()
}
