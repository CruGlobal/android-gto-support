package org.ccci.gto.android.common.okio

import okio.FileSystem
import okio.ForwardingFileSystem
import okio.IOException
import okio.Path
import okio.SYSTEM

private class ReadOnlyFileSystem(delegate: FileSystem = FileSystem.SYSTEM) : ForwardingFileSystem(delegate) {
    override fun appendingSink(file: Path, mustExist: Boolean) = throwReadOnly()
    override fun atomicMove(source: Path, target: Path) = throwReadOnly()
    override fun createDirectory(dir: Path, mustCreate: Boolean) = throwReadOnly()
    override fun createSymlink(source: Path, target: Path) = throwReadOnly()
    override fun delete(path: Path, mustExist: Boolean) = throwReadOnly()
    override fun openReadWrite(file: Path, mustCreate: Boolean, mustExist: Boolean) = throwReadOnly()
    override fun sink(file: Path, mustCreate: Boolean) = throwReadOnly()

    private fun throwReadOnly(): Nothing = throw IOException("Read-only file system")
}

fun FileSystem.readOnly(): FileSystem = ReadOnlyFileSystem(this)
