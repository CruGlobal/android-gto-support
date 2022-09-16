package org.ccci.gto.android.common.okta.authfoundation.credential

import com.okta.authfoundation.credential.TokenStorage
import io.mockk.coVerifyAll
import io.mockk.mockk
import java.util.UUID
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.ccci.gto.android.common.okta.authfoundation.credential.ChangeAwareTokenStorage.Companion.makeChangeAware
import org.junit.Assert.assertSame
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChangeAwareTokenStorageTest {
    @Test
    fun `makeChangeAware()`() = runTest {
        val base = mockk<TokenStorage>(relaxUnitFun = true)
        val storage = base.makeChangeAware()
        val id = UUID.randomUUID().toString()
        storage.add(id)
        coVerifyAll { base.add(id) }
    }

    @Test
    fun `makeChangeAware() - Noop if storage is already change aware`() {
        val storage = mockk<ChangeAwareTokenStorage>()
        assertSame(storage, storage.makeChangeAware())
    }
}
