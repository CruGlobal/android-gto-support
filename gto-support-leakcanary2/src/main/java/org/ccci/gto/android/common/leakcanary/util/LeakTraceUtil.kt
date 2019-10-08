package org.ccci.gto.android.common.leakcanary.util

import shark.Leak

fun Leak.asFakeException(): Exception {
    val firstElement = leakTrace.elements[0]

    return RuntimeException(
        "$classSimpleName leak from ${firstElement.classSimpleName} " +
                "(holder=${firstElement.holder}, type=${firstElement.reference?.type})"
    ).also { exception ->
        exception.stackTrace = leakTrace.elements
            .map { StackTraceElement(it.className, it.reference?.name ?: "leaking", "${it.classSimpleName}.java", 42) }
            .toTypedArray()
    }
}
