package org.ccci.gto.android.common.dagger.okhttp3

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class InterceptorType(val type: Type = Type.INTERCEPTOR) {
    enum class Type { INTERCEPTOR, NETWORK_INTERCEPTOR }
}
