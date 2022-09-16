package org.ccci.gto.android.common.okta.datastore

import com.okta.authfoundation.credential.TokenStorage
import java.util.UUID
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DataStoreTokenStorageTest {
    private val storage = DataStoreTokenStorage(InMemoryDataStore(emptyList()))

    @Test
    fun testAdd() = runTest(UnconfinedTestDispatcher()) {
        val id = UUID.randomUUID().toString()
        assertTrue(storage.entries().isEmpty())

        storage.add(id)
        assertEquals(1, storage.entries().size)
        val entry = storage.entries().single()
        assertEquals(id, entry.identifier)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testAddDuplicate() = runTest(UnconfinedTestDispatcher()) {
        val id = UUID.randomUUID().toString()
        storage.add(id)
        assertEquals(1, storage.entries().size)

        storage.add(id)
    }

    @Test
    fun testReplace() = runTest {
        val id = UUID.randomUUID().toString()
        storage.add(id)
        assertEquals(1, storage.entries().size)

        val entry = TokenStorage.Entry(
            identifier = id,
            token = null,
            tags = mapOf(UUID.randomUUID().toString() to UUID.randomUUID().toString())
        )
        storage.replace(entry)
        assertEquals(entry, storage.entries().single())
    }

    @Test
    fun testReplaceNonExistant() = runTest {
        assertTrue(storage.entries().isEmpty())

        val entry = TokenStorage.Entry(
            identifier = UUID.randomUUID().toString(),
            token = null,
            tags = mapOf(UUID.randomUUID().toString() to UUID.randomUUID().toString())
        )
        storage.replace(entry)
        assertTrue(storage.entries().isEmpty())
    }

    @Test
    fun testRemove() = runTest(UnconfinedTestDispatcher()) {
        val id = UUID.randomUUID().toString()
        storage.add(id)
        assertEquals(1, storage.entries().size)

        storage.remove(id)
        assertTrue(storage.entries().isEmpty())
    }

    @Test
    fun testRemoveNonExistant() = runTest(UnconfinedTestDispatcher()) {
        assertTrue(storage.entries().isEmpty())
        val id = UUID.randomUUID().toString()
        storage.add(id)

        storage.remove("missing-$id")
        assertEquals(1, storage.entries().size)
        val entry = storage.entries().single()
        assertEquals(id, entry.identifier)
    }
}
