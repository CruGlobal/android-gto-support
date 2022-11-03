package org.ccci.gto.support.androidx.annotation

@OptIn(ExperimentalMultiplatform::class)
@OptionalExpectation
@Retention(AnnotationRetention.BINARY)
expect annotation class VisibleForTesting()
