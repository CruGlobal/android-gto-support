package org.ccci.gto.android.common.okta.datastore

import com.okta.authfoundation.credential.Token
import com.okta.authfoundation.credential.TokenStorage
import java.io.ByteArrayOutputStream
import java.util.UUID
import kotlin.random.Random
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TokenStorageEntriesSerializerTest {
    private val entry = TokenStorage.Entry(
        identifier = UUID.randomUUID().toString(),
        token = Token(
            tokenType = UUID.randomUUID().toString(),
            expiresIn = Random.nextInt(),
            accessToken = UUID.randomUUID().toString(),
            scope = UUID.randomUUID().toString(),
            refreshToken = UUID.randomUUID().toString(),
            idToken = UUID.randomUUID().toString(),
            deviceSecret = UUID.randomUUID().toString(),
            issuedTokenType = UUID.randomUUID().toString()
        ),
        tags = mapOf(UUID.randomUUID().toString() to UUID.randomUUID().toString())
    )

    @Test
    fun testSerialization() = runTest {
        val data = ByteArrayOutputStream().use {
            TokenStorageEntriesSerializer.writeTo(listOf(entry), it)
            it.toByteArray()
        }

        val decoded = data.inputStream().use {
            TokenStorageEntriesSerializer.readFrom(it)
        }

        assertEquals(entry, decoded.single())
        assertNotSame(entry, decoded.single())
    }
}
