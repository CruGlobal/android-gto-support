package org.ccci.gto.support.androidx.annotation

@OptIn(ExperimentalMultiplatform::class)
@OptionalExpectation
@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.FIELD,
    AnnotationTarget.FILE,
)
expect annotation class RestrictTo(vararg val value: RestrictToScope)
expect enum class RestrictToScope { LIBRARY, LIBRARY_GROUP, TESTS, SUBCLASSES }
