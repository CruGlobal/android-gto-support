package org.ccci.gto.android.common.dagger.coroutines

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class DispatcherType(val value: Type) {
    enum class Type { DEFAULT, MAIN, MAIN_IMMEDIATE, IO, UNCONFINED }
}
