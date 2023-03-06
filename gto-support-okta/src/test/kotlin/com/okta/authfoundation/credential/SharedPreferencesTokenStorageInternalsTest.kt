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
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
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

    @Before
    fun setup() {
        mockkStatic(MasterKeys::class, EncryptedSharedPreferences::class)
        every { MasterKeys.getOrCreate(any()) } returns "key"
        every { EncryptedSharedPreferences.create(any(), any(), any(), any(), any()) } returns sharedPreferences
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

    @Test(expected = SharedPreferencesException::class)
    fun `getSharedPreferences() - Propagates Exceptions`() {
        val preferences = SharedPreferencesTokenStorageInternals.create(oidcClient, context, keySpec)
        every { MasterKeys.getOrCreate(any()) } throws SharedPreferencesException()
        SharedPreferencesTokenStorageInternals.getSharedPreferences(preferences)
    }
    // endregion getSharedPreferences()
}
