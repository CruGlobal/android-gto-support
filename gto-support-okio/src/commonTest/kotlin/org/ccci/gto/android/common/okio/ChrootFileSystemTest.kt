package org.ccci.gto.android.common.okio

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import okio.FileSystem
import okio.ForwardingFileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM

@OptIn(ExperimentalUuidApi::class)
class ChrootFileSystemTest {
    private val tmpDir = FileSystem.SYSTEM_TEMPORARY_DIRECTORY / Uuid.random().toString()
    private fun tmpFile() = tmpDir / (Uuid.random().toString() + ".tmp")

    private val baseFs = FileSystem.SYSTEM
    private val chrootFs = baseFs.chroot(tmpDir) as ForwardingFileSystem

    @BeforeTest
    fun createTmpDir() {
        baseFs.createDirectory(tmpDir)
    }

    @AfterTest
    fun cleanupTmpDir() {
        baseFs.deleteRecursively(tmpDir)
    }

    @Test
    fun testInvalidRootPath() {
        assertFails { baseFs.chroot("..".toPath()) }
    }

    @Test
    fun testOnPathParameter() {
        assertEquals(tmpDir, chrootFs.onPathParameter("/".toPath(), "foo", "bar"))
        assertEquals(tmpDir, chrootFs.onPathParameter("/a/../..".toPath(), "foo", "bar"))
        assertEquals(tmpDir, chrootFs.onPathParameter("../../..".toPath(), "foo", "bar"))

        assertEquals(tmpDir / "tmp.ext", chrootFs.onPathParameter("/tmp.ext".toPath(), "foo", "bar"))
        assertEquals(tmpDir / "tmp.ext", chrootFs.onPathParameter("tmp.ext".toPath(), "foo", "bar"))
        assertEquals(tmpDir / "tmp.ext", chrootFs.onPathParameter("../tmp.ext".toPath(), "foo", "bar"))
    }

    @Test
    fun testList() {
        val files = List(10) { tmpFile() }
            .onEach { baseFs.write(it) { writeUtf8("test file") } }
            .map { "/".toPath().resolve(it.name) }
            .toSet()

        assertEquals(files, chrootFs.list("/".toPath()).toSet())
    }
}
