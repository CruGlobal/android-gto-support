package org.ccci.gto.android.common.util.kotlin

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private class ThreadLocalDelegate<T>(initializer: () -> T) : ReadWriteProperty<Any, T> {
    private val threadLocal = ThreadLocalWithInitializer(initializer)

    override operator fun getValue(thisRef: Any, property: KProperty<*>): T = threadLocal.get() as T
    override operator fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        threadLocal.set(value)
    }
}

private class ThreadLocalWithInitializer<T>(private val initializer: () -> T) : ThreadLocal<T>() {
    override fun initialValue(): T = initializer()
}

fun <T> threadLocal(initializer: () -> T): ReadWriteProperty<Any, T> = ThreadLocalDelegate(initializer)
fun <T> threadLocal() = threadLocal<T?> { null }
