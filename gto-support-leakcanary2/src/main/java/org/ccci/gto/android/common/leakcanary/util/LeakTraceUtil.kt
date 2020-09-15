package org.ccci.gto.android.common.leakcanary.util

import shark.LeakTrace

fun LeakTrace.asFakeException(): Exception {
    val firstElement = referencePath[0]

    return RuntimeException(
        "${leakingObject.classSimpleName} leak from ${firstElement.originObject.classSimpleName} " +
            "(name=${firstElement.referenceName}, type=${firstElement.referenceType})"
    ).also { exception ->
        exception.stackTrace = referencePath
            .map {
                StackTraceElement(
                    it.originObject.className, it.referenceName, "${it.originObject.classSimpleName}.java", 42
                )
            }
            .toTypedArray()
    }
}
