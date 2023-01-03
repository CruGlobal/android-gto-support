package org.ccci.gto.android.common.leakcanary.util

import shark.LeakTrace

fun LeakTrace.asFakeException(): Exception {
    val lastElement = referencePath.last()

    return RuntimeException(
        "${leakingObject.classSimpleName} leak from ${lastElement.originObject.classSimpleName} " +
            "(name=${lastElement.referenceName}, type=${lastElement.referenceType})"
    ).also { exception ->
        exception.stackTrace = referencePath
            .reversed()
            .map {
                StackTraceElement(
                    it.originObject.className,
                    it.referenceName,
                    "${it.originObject.classSimpleName}.java",
                    -1
                )
            }
            .toTypedArray()
    }
}
