package org.ccci.gto.android.common.kotlin.coroutines.flow

/**
 * Wrapper class for StateFlow values that supports indicating if the current value is the initial value or not.
 */
open class StateFlowValue<T>(val value: T) {
    class Initial<T>(value: T) : StateFlowValue<T>(value) {
        override val isInitial get() = true
    }

    open val isInitial get() = false

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StateFlowValue<*>
        if (value != other.value) return false
        return true
    }

    override fun hashCode() = value?.hashCode() ?: 0
}
