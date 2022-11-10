package org.ccci.gto.support.androidx.test.junit.runners

import kotlin.reflect.KClass

@OptIn(ExperimentalMultiplatform::class)
@OptionalExpectation
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
expect annotation class RunOnAndroidWith(val value: KClass<out Runner>)
expect abstract class Runner
expect class AndroidJUnit4 : Runner
