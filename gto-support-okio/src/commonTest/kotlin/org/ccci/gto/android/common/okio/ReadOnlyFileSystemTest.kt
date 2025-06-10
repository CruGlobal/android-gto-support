package org.ccci.gto.android.common.okio

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import okio.FileSystem
import okio.IOException
import okio.SYSTEM
import okio.buffer
import okio.use

@OptIn(ExperimentalUuidApi::class)
class ReadOnlyFileSystemTest {
    private val baseFs = FileSystem.SYSTEM
    private val readOnlyFs = baseFs.readOnly()

    private val tmpDir = FileSystem.SYSTEM_TEMPORARY_DIRECTORY / Uuid.random().toString()
    private fun tmpFile() = tmpDir / (Uuid.random().toString() + ".tmp")

    @BeforeTest
    fun createTmpDir() {
        baseFs.createDirectory(tmpDir)
    }

    @AfterTest
    fun cleanupTmpDir() {
        baseFs.deleteRecursively(tmpDir)
    }

    @Test
    fun testAppendingSink() {
        val file = tmpFile()
        baseFs.write(file) { writeUtf8("test") }

        assertFailsWith(IOException::class) { readOnlyFs.appendingSink(file) }
        assertEquals("test", baseFs.read(file) { readUtf8() })

        baseFs.appendingSink(file).buffer().use { it.writeUtf8(" file") }
        assertEquals("test file", baseFs.read(file) { readUtf8() })
    }

    @Test
    fun testAtomicMove() {
        val source = tmpFile()
        baseFs.write(source) { writeUtf8("test file") }
        val target = tmpFile()

        assertFailsWith(IOException::class) { readOnlyFs.atomicMove(source, target) }
        assertTrue(baseFs.exists(source))
        assertFalse(baseFs.exists(target))

        baseFs.atomicMove(source, target)
        assertFalse(baseFs.exists(source))
        assertTrue(baseFs.exists(target))
    }

    @Test
    fun testCopy() {
        val source = tmpFile()
        baseFs.write(source) { writeUtf8("test file") }
        val target = tmpFile()

        assertFailsWith(IOException::class) { readOnlyFs.copy(source, target) }
        assertFalse(baseFs.exists(target))

        baseFs.copy(source, target)
        assertTrue(baseFs.exists(target))
    }

    @Test
    fun testCreateDirectory() {
        val dir = tmpFile()

        assertFailsWith(IOException::class) { readOnlyFs.createDirectory(dir) }
        assertFalse(baseFs.exists(dir))

        baseFs.createDirectory(dir)
        assertTrue(baseFs.exists(dir))
    }

    @Test
    fun testCreateSymlink() {
        val link = tmpFile()
        val target = tmpFile()
        baseFs.write(target) { writeUtf8("test file") }

        assertFailsWith(IOException::class) { readOnlyFs.createSymlink(link, target) }
        assertFalse(baseFs.exists(link))

        baseFs.createSymlink(link, target)
        assertTrue(baseFs.exists(link))
    }

    @Test
    fun testDelete() {
        val file = tmpFile()
        baseFs.write(file) { writeUtf8("test file") }

        assertFailsWith(IOException::class) { readOnlyFs.delete(file) }
        assertTrue(baseFs.exists(file))

        baseFs.delete(file)
        assertFalse(baseFs.exists(file))
    }

    @Test
    fun testDeleteRecursively() {
        val dir = tmpFile()
        baseFs.createDirectory(dir)
        val file = dir / "file"
        baseFs.write(file) { writeUtf8("test file") }

        assertFailsWith(IOException::class) { readOnlyFs.deleteRecursively(dir) }
        assertTrue(baseFs.exists(dir))
        assertTrue(baseFs.exists(file))

        baseFs.deleteRecursively(dir)
        assertFalse(baseFs.exists(dir))
        assertFalse(baseFs.exists(file))
    }

    @Test
    fun testOpenReadWrite() {
        val file = tmpFile()

        assertFailsWith(IOException::class) { readOnlyFs.openReadWrite(file) }
        assertFalse(baseFs.exists(file))

        baseFs.openReadWrite(file).close()
        assertTrue(baseFs.exists(file))
    }

    @Test
    fun testSink() {
        val file = tmpFile()

        assertFailsWith(IOException::class) { readOnlyFs.sink(file) }
        assertFalse(baseFs.exists(file))

        baseFs.sink(file).buffer().use { it.writeUtf8("test file") }
        assertTrue(baseFs.exists(file))
    }

    @Test
    fun testRead() {
        val file = tmpFile()
        baseFs.write(file) { writeUtf8("test file") }

        assertEquals("test file", readOnlyFs.read(file) { readUtf8() })
    }
}
