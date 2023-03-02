package org.ccci.gto.support.androidx.annotation

@OptIn(ExperimentalMultiplatform::class)
@OptionalExpectation
@Target(AnnotationTarget.CONSTRUCTOR, AnnotationTarget.PROPERTY_GETTER)
expect annotation class RestrictTo(vararg val value: RestrictToScope)
expect enum class RestrictToScope { LIBRARY_GROUP, TESTS, SUBCLASSES }
