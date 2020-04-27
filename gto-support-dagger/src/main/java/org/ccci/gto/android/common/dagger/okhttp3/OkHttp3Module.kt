package org.ccci.gto.android.common.dagger.okhttp3

import dagger.Module
import dagger.multibindings.Multibinds
import okhttp3.Interceptor

@Module
abstract class OkHttp3Module {
    @Multibinds
    @InterceptorType(InterceptorType.Type.INTERCEPTOR)
    abstract fun interceptors(): Set<Interceptor>

    @Multibinds
    @InterceptorType(InterceptorType.Type.NETWORK_INTERCEPTOR)
    abstract fun networkInterceptors(): Set<Interceptor>
}
