package org.ccci.gto.android.common.okio

import okio.FileSystem
import okio.ForwardingFileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.SYSTEM

private class ChrootFileSystem(private val root: Path, delegate: FileSystem = FileSystem.SYSTEM) :
    ForwardingFileSystem(delegate) {
    companion object {
        val ROOT_PATH = "/".toPath()
    }

    init {
        require(root.isAbsolute) { "root must be absolute" }
    }

    override fun onPathParameter(path: Path, functionName: String, parameterName: String): Path {
        val absolutePath = when {
            !path.isAbsolute -> ROOT_PATH.resolve(path, normalize = true)
            else -> path.normalized()
        }
        return root.resolve(absolutePath.relativeTo(ROOT_PATH), normalize = true)
    }

    override fun onPathResult(path: Path, functionName: String) = ROOT_PATH.resolve(path.relativeTo(root))
}

fun FileSystem.chroot(root: Path): FileSystem = ChrootFileSystem(root, this)
